package com.allvens.allworkouts.base;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple Service Locator pattern for dependency injection
 * Manages dependencies and provides them to managers and activities
 * This is a lightweight alternative to full DI frameworks for this app
 */
public class ServiceLocator {
    
    private static ServiceLocator instance;
    private final Map<Class<?>, Object> services = new HashMap<>();
    private Context applicationContext;
    
    private ServiceLocator() {
        // Private constructor for singleton
    }
    
    /**
     * Get the singleton instance of ServiceLocator
     */
    public static ServiceLocator getInstance() {
        if (instance == null) {
            synchronized (ServiceLocator.class) {
                if (instance == null) {
                    instance = new ServiceLocator();
                }
            }
        }
        return instance;
    }
    
    /**
     * Initialize the service locator with application context
     * Should be called from Application class or main activity
     */
    public void initialize(Context context) {
        this.applicationContext = context.getApplicationContext();
        registerDefaultServices();
    }
    
    /**
     * Register a service instance
     */
    public <T> void register(Class<T> serviceClass, T instance) {
        services.put(serviceClass, instance);
    }
    
    /**
     * Get a service instance
     */
    @SuppressWarnings("unchecked")
    public <T> T get(Class<T> serviceClass) {
        T service = (T) services.get(serviceClass);
        if (service == null) {
            throw new IllegalStateException("Service not registered: " + serviceClass.getName());
        }
        return service;
    }
    
    /**
     * Check if a service is registered
     */
    public boolean isRegistered(Class<?> serviceClass) {
        return services.containsKey(serviceClass);
    }
    
    /**
     * Get application context
     */
    public Context getApplicationContext() {
        if (applicationContext == null) {
            throw new IllegalStateException("ServiceLocator not initialized");
        }
        return applicationContext;
    }
    
    /**
     * Clear all services (useful for testing)
     */
    public void clear() {
        services.clear();
        applicationContext = null;
    }
    
    /**
     * Register default services that are commonly used across the app
     */
    private void registerDefaultServices() {
        // Register any default services here if needed
        // For now, we'll keep it simple and register services as needed
    }
    
    /**
     * Factory interface for creating services
     */
    public interface ServiceFactory<T> {
        T create();
    }
    
    /**
     * Register a service factory that will create the service on first access
     */
    public <T> void registerFactory(Class<T> serviceClass, ServiceFactory<T> factory) {
        // Simple implementation - create immediately
        // Could be enhanced to support lazy creation if needed
        register(serviceClass, factory.create());
    }
}