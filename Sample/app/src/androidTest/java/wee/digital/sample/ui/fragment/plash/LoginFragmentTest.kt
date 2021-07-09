package wee.digital.sample.ui.fragment.plash

import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import wee.digital.sample.databinding.LoginBinding
import wee.digital.sample.ui.fragment.login.LoginVM


/**
 * -------------------------------------------------------------------------------------------------
 *
 * @Project: Sample
 * @Created: Huy 2021/07/09
 * @Organize: Wee Digital
 * @Description: ...
 * All Right Reserved
 * -------------------------------------------------------------------------------------------------
 */
class LoginFragmentTest {

    lateinit var bind: LoginBinding
    lateinit var vm: LoginVM

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }


    @Test
    fun testLogin() {
        getInstrumentation().runOnMainSync {
            bind.inputViewEmail.text = ""
            bind.inputViewPassword.text = ""
            bind.viewLogin.performClick()
            Assert.assertNotNull(bind.inputViewEmail.error)
            Assert.assertNotNull(bind.inputViewPassword.error)
        }
        getInstrumentation().runOnMainSync {
            bind.inputViewEmail.text = "aaaa"
            bind.inputViewPassword.text = "123456789"
            bind.viewLogin.performClick()
            Assert.assertNotNull(bind.inputViewEmail.error)
            Assert.assertNotNull(bind.inputViewPassword.error)
        }
        getInstrumentation().runOnMainSync {
            bind.inputViewEmail.text = "aaaa"
            bind.inputViewPassword.text = "123456789"
            bind.viewLogin.performClick()
            Assert.assertNotNull(bind.inputViewEmail.error)
            Assert.assertNotNull(bind.inputViewPassword.error)
        }
    }


}