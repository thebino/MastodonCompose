/*
 * This file is part of Dodo.
 *
 * Dodo is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Dodo is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Dodo. If not, see <https://www.gnu.org/licenses/>.
 */
package social.androiddev.common.network.model

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import social.androiddev.common.readBinaryResource
import kotlin.test.Test
import kotlin.test.assertEquals

class AccountTests {
    @Test
    fun `deserialize required fields should succeed`() {
        // given
        val byteArray: ByteArray = readBinaryResource("src/commonTest/resources/response_account_required.json")
        val json: String = byteArray.decodeToString()

        // when
        val account = Json.decodeFromString<Account>(json)

        // then

        assertEquals(expected = "23634", actual = account.id)
    }
}
