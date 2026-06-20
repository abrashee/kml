package com.kml.services.order.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InternalUserProfileService {

    private final UserProfileClient client;
    private final String internalToken;

    public InternalUserProfileService(
        UserProfileClient client,
        @Value("${kml.internal-service-token}") String internalToken) {
        this.client = client;
        this.internalToken = internalToken;
    }

    public UserProfileClient.ShippingAddressResponse getShippingAddress(Long userId) {
        return client.getShippingAddress(userId, internalToken);
    }
}
