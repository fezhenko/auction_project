package org.example.apigateway.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "users-service", url = "{services.users-service.url}/api/v1/users")
public interface UserClient {
}
