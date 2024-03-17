package com.burujiyaseer.passwordmanager.domain.usecase.get_website_fav_icon_url

interface SaveFavIconFromWebsiteUrl {
    /**retrieves the fav icon from a website, and saves it locally in a file.
     * @return the uri of the saved file or null in any other error case
    */
    suspend operator fun invoke(websiteUrl: String): String?
}