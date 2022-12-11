/*
 * This file is part of Dodo.
 *
 * Dodo is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Dodo is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Dodo. If not, see <https://www.gnu.org/licenses/>.
 */
package social.androiddev.common.repository

import social.androiddev.common.network.MastodonApi
import social.androiddev.common.persistence.AuthenticationDatabase
import social.androiddev.common.persistence.localstorage.DodoAuthStorage
import social.androiddev.domain.authentication.model.ApplicationOAuthToken
import social.androiddev.domain.authentication.model.NewAppOAuthToken
import social.androiddev.domain.authentication.repository.AuthenticationRepository

internal class AuthenticationRepositoryImpl(
    private val mastodonApi: MastodonApi,
    private val database: AuthenticationDatabase,
    private val settings: DodoAuthStorage,
) : AuthenticationRepository {

    override suspend fun createApplicationClient(
        domain: String,
        clientName: String,
        redirectUris: String,
        scopes: String,
        website: String?
    ): NewAppOAuthToken? {
        val application = mastodonApi.createApplication(
            domain = domain,
            clientName = clientName,
            redirectUris = redirectUris,
            scopes = scopes,
            website = website
        ).getOrNull()

        return application?.let {
            NewAppOAuthToken(
                clientId = it.clientId,
                clientSecret = it.clientSecret,
                redirectUri = redirectUris,
            )
        }
    }

    override suspend fun saveApplication(token: NewAppOAuthToken, domain: String) {
        database.applicationQueries.insertApplication(
            instance = domain,
            client_id = token.clientId,
            client_secret = token.clientSecret,
            redirect_uri = token.redirectUri,
        )
        settings.currentDomain = domain
    }

    override suspend fun getApplicationOAuthToken(server: String): ApplicationOAuthToken? {
        return database
            .applicationQueries
            .selectByServer(server)
            .executeAsOneOrNull()
            ?.let {
                ApplicationOAuthToken(
                    server = it.instance,
                    clientId = it.client_id,
                    clientSecret = it.client_secret,
                    redirectUri = it.client_secret // TODO fix
                )
            }
    }

    override suspend fun createAccessToken(authCode: String, server: String, scope: String): String? {
        return getApplicationOAuthToken(server)?.let { oAuthToken ->
            mastodonApi
                .createAccessToken(
                    domain = server,
                    clientId = oAuthToken.clientId,
                    clientSecret = oAuthToken.clientSecret,
                    redirectUri = oAuthToken.redirectUri,
                    grantType = "authorization_code",
                    code = authCode,
                    scope = "read write follow push",
                )
                .getOrNull()
                ?.accessToken
        }
    }

    override fun saveAccessToken(server: String, token: String) {
        settings.saveAccessToken(server = server, token = token)
    }

    override val selectedServer: String? = settings.currentDomain
}
