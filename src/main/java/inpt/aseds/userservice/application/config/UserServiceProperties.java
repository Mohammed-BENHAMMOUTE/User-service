package inpt.aseds.userservice.application.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the user service application.
 * Maps values from application.yaml under 'app.user' prefix.
 */
@Component
@ConfigurationProperties(prefix = "app.user")
public class UserServiceProperties {
    
    private final Validation validation = new Validation();
    private final Pagination pagination = new Pagination();
    private final Cache cache = new Cache();
    
    public Validation getValidation() {
        return validation;
    }
    
    public Pagination getPagination() {
        return pagination;
    }
    
    public Cache getCache() {
        return cache;
    }
    
    public static class Validation {
        private final Username username = new Username();
        private final Email email = new Email();
        private final Bio bio = new Bio();
        private final Search search = new Search();
        
        public Username getUsername() {
            return username;
        }
        
        public Email getEmail() {
            return email;
        }
        
        public Bio getBio() {
            return bio;
        }
        
        public Search getSearch() {
            return search;
        }
        
        public static class Username {
            private int minLength = 3;
            private int maxLength = 100;
            
            public int getMinLength() {
                return minLength;
            }
            
            public void setMinLength(int minLength) {
                this.minLength = minLength;
            }
            
            public int getMaxLength() {
                return maxLength;
            }
            
            public void setMaxLength(int maxLength) {
                this.maxLength = maxLength;
            }
        }
        
        public static class Email {
            private int maxLength = 100;
            
            public int getMaxLength() {
                return maxLength;
            }
            
            public void setMaxLength(int maxLength) {
                this.maxLength = maxLength;
            }
        }
        
        public static class Bio {
            private int maxLength = 500;
            
            public int getMaxLength() {
                return maxLength;
            }
            
            public void setMaxLength(int maxLength) {
                this.maxLength = maxLength;
            }
        }
        
        public static class Search {
            private int minQueryLength = 2;
            private int maxResults = 100;
            
            public int getMinQueryLength() {
                return minQueryLength;
            }
            
            public void setMinQueryLength(int minQueryLength) {
                this.minQueryLength = minQueryLength;
            }
            
            public int getMaxResults() {
                return maxResults;
            }
            
            public void setMaxResults(int maxResults) {
                this.maxResults = maxResults;
            }
        }
    }
    
    public static class Pagination {
        private int defaultPageSize = 20;
        private int maxPageSize = 100;
        
        public int getDefaultPageSize() {
            return defaultPageSize;
        }
        
        public void setDefaultPageSize(int defaultPageSize) {
            this.defaultPageSize = defaultPageSize;
        }
        
        public int getMaxPageSize() {
            return maxPageSize;
        }
        
        public void setMaxPageSize(int maxPageSize) {
            this.maxPageSize = maxPageSize;
        }
    }
    
    public static class Cache {
        private boolean enabled = true;
        private String ttl = "300s";
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public String getTtl() {
            return ttl;
        }
        
        public void setTtl(String ttl) {
            this.ttl = ttl;
        }
    }
}
