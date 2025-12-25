package com.healthtourism.jpa.util;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.UUID;

/**
 * Sequential UUID Generator for better database performance
 * 
 * Unlike standard UUIDs (UUID.randomUUID()), sequential UUIDs are ordered,
 * which improves database index performance and reduces fragmentation.
 * 
 * Uses time-based component (timestamp) + node identifier (MAC address) + random component
 * This ensures:
 * 1. Better index locality (similar to auto-increment IDs)
 * 2. Uniqueness across distributed systems
 * 3. Security (harder to guess than sequential integers)
 * 
 * Implementation based on COMB GUID (Combined GUID) approach
 */
public class SequentialUUIDGenerator implements IdentifierGenerator {
    
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final long nodeIdentifier;
    
    static {
        // Generate a unique node identifier from MAC address
        nodeIdentifier = generateNodeIdentifier();
    }
    
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        return generateSequentialUUID();
    }
    
    /**
     * Generate a sequential UUID (time-based + node + random)
     */
    public static UUID generateSequentialUUID() {
        long timestamp = System.currentTimeMillis();
        
        // Use 48 bits for timestamp (milliseconds since epoch)
        // This gives us about 8925 years of range
        long timestampBits = timestamp & 0x0000FFFFFFFFFFFFL;
        
        // Shift timestamp to high bits
        long highBits = (timestampBits << 16) | (0x4000L << 48); // Version 4 indicator
        
        // Use 14 bits for node identifier, 2 bits for variant
        long lowBits = ((nodeIdentifier & 0x3FFFL) << 48) | 
                       (0x8000000000000000L) | // Variant bits
                       (secureRandom.nextLong() & 0x0000FFFFFFFFFFFFL);
        
        return new UUID(highBits, lowBits);
    }
    
    /**
     * Generate a unique node identifier from MAC address
     * Falls back to random if MAC address cannot be determined
     */
    private static long generateNodeIdentifier() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = networkInterfaces.nextElement();
                if (!ni.isLoopback() && ni.isUp()) {
                    byte[] mac = ni.getHardwareAddress();
                    if (mac != null && mac.length >= 6) {
                        // Use first 6 bytes of MAC address to create node identifier
                        // Convert 6-byte MAC to 48-bit value
                        long nodeId = 0;
                        for (int i = 0; i < Math.min(6, mac.length); i++) {
                            nodeId = (nodeId << 8) | (mac[i] & 0xFF);
                        }
                        return nodeId & 0x0000FFFFFFFFFFFFL;
                    }
                }
            }
        } catch (Exception e) {
            // Fallback to random
        }
        
        // Fallback: use random node identifier
        return secureRandom.nextLong() & 0x0000FFFFFFFFFFFFL;
    }
}

