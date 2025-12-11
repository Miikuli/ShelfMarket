package org.example.shelf_market.services;

import org.example.shelf_market.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@Primary // Этот бин будет использоваться по умолчанию
public class UserServiceProxy implements UserServiceInterface {

    private final UserServiceInterface targetService;
    private final ConcurrentHashMap<String, CacheEntry<UserDTO>> cache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CacheEntry<List<UserDTO>>> listCache = new ConcurrentHashMap<>();

    // Время жизни кэша в миллисекундах (1 час)
    private static final long CACHE_TTL = TimeUnit.HOURS.toMillis(1);

    @Autowired
    public UserServiceProxy(@Qualifier("userServiceTarget") UserServiceInterface targetService) {
        this.targetService = targetService;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        String cacheKey = "all_users";
        CacheEntry<List<UserDTO>> cached = listCache.get(cacheKey);

        if (cached != null && !cached.isExpired()) {
            System.out.println("Cache HIT for: " + cacheKey);
            return cached.getData();
        }

        System.out.println("Cache MISS for: " + cacheKey);
        List<UserDTO> users = targetService.getAllUsers();
        listCache.put(cacheKey, new CacheEntry<>(users));
        return users;
    }

    @Override
    public UserDTO getUserById(UUID id) {
        String cacheKey = "user_id_" + id;
        CacheEntry<UserDTO> cached = cache.get(cacheKey);

        if (cached != null && !cached.isExpired()) {
            System.out.println("Cache HIT for: " + cacheKey);
            return cached.getData();
        }

        System.out.println("Cache MISS for: " + cacheKey);
        UserDTO user = targetService.getUserById(id);
        cache.put(cacheKey, new CacheEntry<>(user));
        return user;
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        String cacheKey = "user_username_" + username;
        CacheEntry<UserDTO> cached = cache.get(cacheKey);

        if (cached != null && !cached.isExpired()) {
            System.out.println("Cache HIT for: " + cacheKey);
            return cached.getData();
        }

        System.out.println("Cache MISS for: " + cacheKey);
        UserDTO user = targetService.getUserByUsername(username);
        cache.put(cacheKey, new CacheEntry<>(user));
        return user;
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        // При создании нового пользователя инвалидируем кэш списка
        invalidateListCache();
        UserDTO createdUser = targetService.createUser(userDTO);

        // Кэшируем нового пользователя
        cache.put("user_id_" + createdUser.getId(), new CacheEntry<>(createdUser));
        cache.put("user_username_" + createdUser.getUsername(), new CacheEntry<>(createdUser));

        return createdUser;
    }

    @Override
    public UserDTO updateUser(UUID id, UserDTO userDTO) {
        // Получаем старые данные для инвалидации кэша по username
        UserDTO oldUser = targetService.getUserById(id);

        // Инвалидируем кэши
        invalidateUserCache(id);
        if (oldUser != null && !oldUser.getUsername().equals(userDTO.getUsername())) {
            cache.remove("user_username_" + oldUser.getUsername());
        }
        invalidateListCache();

        UserDTO updatedUser = targetService.updateUser(id, userDTO);

        // Кэшируем обновленные данные
        cache.put("user_id_" + id, new CacheEntry<>(updatedUser));
        cache.put("user_username_" + updatedUser.getUsername(), new CacheEntry<>(updatedUser));

        return updatedUser;
    }

    @Override
    public void deleteUser(UUID id) {
        // Получаем пользователя перед удалением для инвалидации кэша
        UserDTO userToDelete = targetService.getUserById(id);

        // Инвалидируем кэши
        invalidateUserCache(id);
        if (userToDelete != null) {
            cache.remove("user_username_" + userToDelete.getUsername());
        }
        invalidateListCache();

        targetService.deleteUser(id);
    }

    @Override
    public List<UserDTO> searchUsers(String query) {
        return targetService.searchUsers(query);
    }

    @Override
    public boolean userExists(UUID id) {
        return targetService.userExists(id);
    }

    // Методы для управления кэшем
    private void invalidateUserCache(UUID userId) {
        cache.remove("user_id_" + userId);
    }

    private void invalidateListCache() {
        listCache.remove("all_users");
    }

    public void clearAllCache() {
        cache.clear();
        listCache.clear();
        System.out.println("All cache cleared");
    }

    public int getCacheSize() {
        return cache.size() + listCache.size();
    }

    // Внутренний класс для хранения кэшированных данных с TTL
    private static class CacheEntry<T> {
        private final T data;
        private final long timestamp;

        public CacheEntry(T data) {
            this.data = data;
            this.timestamp = System.currentTimeMillis();
        }

        public T getData() {
            return data;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_TTL;
        }
    }
}