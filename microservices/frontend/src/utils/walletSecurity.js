/**
 * Wallet Security Utilities
 * Security best practices for Web3 wallet connections
 */

import { isAddress, getAddress } from 'viem';

/**
 * Validate Ethereum address format
 * @param {string} address - Wallet address to validate
 * @returns {boolean} - True if address is valid
 */
export const isValidAddress = (address) => {
  if (!address || typeof address !== 'string') {
    return false;
  }
  
  try {
    // viem's isAddress performs checksum validation
    return isAddress(address);
  } catch (error) {
    return false;
  }
};

/**
 * Normalize address to checksum format
 * @param {string} address - Wallet address to normalize
 * @returns {string} - Checksummed address
 */
export const normalizeAddress = (address) => {
  if (!isValidAddress(address)) {
    throw new Error('Invalid address format');
  }
  
  try {
    return getAddress(address); // Returns checksummed address
  } catch (error) {
    throw new Error('Failed to normalize address');
  }
};

/**
 * Sanitize address for display (prevent phishing)
 * @param {string} address - Wallet address
 * @returns {string} - Sanitized address for display
 */
export const sanitizeAddressForDisplay = (address) => {
  if (!isValidAddress(address)) {
    return '';
  }
  
  const normalized = normalizeAddress(address);
  return `${normalized.slice(0, 6)}...${normalized.slice(-4)}`;
};

/**
 * Compare two addresses (case-insensitive, checksum-aware)
 * @param {string} address1 - First address
 * @param {string} address2 - Second address
 * @returns {boolean} - True if addresses match
 */
export const compareAddresses = (address1, address2) => {
  if (!isValidAddress(address1) || !isValidAddress(address2)) {
    return false;
  }
  
  try {
    return getAddress(address1).toLowerCase() === getAddress(address2).toLowerCase();
  } catch (error) {
    return false;
  }
};

/**
 * Validate chain ID is supported
 * @param {number} chainId - Chain ID to validate
 * @param {number[]} supportedChains - Array of supported chain IDs
 * @returns {boolean} - True if chain is supported
 */
export const isSupportedChain = (chainId, supportedChains = [137, 80001]) => {
  return supportedChains.includes(Number(chainId));
};

/**
 * Security: Verify connector is safe
 * @param {object} connector - Wagmi connector object
 * @returns {boolean} - True if connector is safe to use
 */
export const isSafeConnector = (connector) => {
  if (!connector || !connector.id) {
    return false;
  }
  
  // Whitelist of known safe connectors
  const safeConnectorIds = ['metaMask', 'injected', 'walletConnect', 'coinbaseWallet'];
  return safeConnectorIds.includes(connector.id);
};

