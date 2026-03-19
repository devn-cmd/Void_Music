package com.freestream.util

/**
 * API Keys Configuration for FreeStream Music App
 * 
 * IMPORTANT SECURITY NOTES:
 * - Add this file to .gitignore before committing to GitHub
 * - Never share these keys publicly or in version control
 * - Regenerate keys immediately if exposed
 * - These keys are for your personal use only
 * 
 * To configure:
 * 1. Replace JAMENDO_CLIENT_ID with your Jamendo Client ID
 * 2. Replace FREESOUND_API_KEY with your Freesound API Key
 * 
 * To get your free API keys:
 * - Jamendo: https://devportal.jamendo.com/ 
 * - Freesound: https://freesound.org/apiv2/apply/
 */
object ApiKeys {
    
    /**
     * Jamendo Client ID
     * 
     * Register for free at: https://devportal.jamendo.com/
     * Required for accessing Jamendo's 400,000+ CC-licensed tracks
     * 
     * TODO: Replace with your actual Jamendo Client ID
     */
    const val JAMENDO_CLIENT_ID = "YOUR_JAMENDO_CLIENT_ID_HERE"
    
    /**
     * Freesound API Key
     * 
     * Register for free at: https://freesound.org/apiv2/apply/
     * Required for accessing Freesound's sound effects and samples
     * 
     * TODO: Replace with your actual Freesound API Key
     */
    const val FREESOUND_API_KEY = "YOUR_FREESOUND_API_KEY_HERE"
    
    /**
     * Validates that API keys have been configured.
     * 
     * @return true if both required keys are configured
     */
    fun areKeysConfigured(): Boolean {
        return JAMENDO_CLIENT_ID != "YOUR_JAMENDO_CLIENT_ID_HERE" &&
               FREESOUND_API_KEY != "YOUR_FREESOUND_API_KEY_HERE" &&
               JAMENDO_CLIENT_ID.isNotBlank() &&
               FREESOUND_API_KEY.isNotBlank()
    }
    
    /**
     * Gets a list of missing API keys for error messaging.
     * 
     * @return List of missing key names
     */
    fun getMissingKeys(): List<String> {
        val missing = mutableListOf<String>()
        if (JAMENDO_CLIENT_ID == "YOUR_JAMENDO_CLIENT_ID_HERE" || JAMENDO_CLIENT_ID.isBlank()) {
            missing.add("Jamendo Client ID")
        }
        if (FREESOUND_API_KEY == "YOUR_FREESOUND_API_KEY_HERE" || FREESOUND_API_KEY.isBlank()) {
            missing.add("Freesound API Key")
        }
        return missing
    }
}
