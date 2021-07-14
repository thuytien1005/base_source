package wee.digital.sample.ui.fragment

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import wee.digital.sample.R
import wee.digital.sample.databinding.LoginBinding
import wee.digital.sample.ui.fragment.sign_in.SignInVM
import wee.digital.sample.ui.fragment.plash.SplashFragment

@RunWith(AndroidJUnit4::class)
class SignInFragmentTest {

    @Mock
    lateinit var bind: LoginBinding

    @Mock
    private lateinit var vm: SignInVM

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        val splashFragment = launchFragmentInContainer<SplashFragment>()
        splashFragment.onFragment { fragment ->
            navController.setGraph(R.navigation.main_graph)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
    }

    @After
    fun tearDown() {
    }

    @Test
    fun login_input_invalid() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            bind.inputViewEmail.text = "wrong email format"
            bind.inputViewPassword.text = ""
            bind.viewLogin.performClick()
            Assert.assertNotNull(bind.inputViewEmail.error)
            Assert.assertNotNull(bind.inputViewPassword.error)
        }
        //assertEquals(vm.liveData1.getOrAwaitValue(), "foo")

    }

    @Test
    fun login_input_valid_and_wrong_email_password() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            bind.inputViewEmail.text = "wrong@email.com"
            bind.inputViewPassword.text = "123456"
            bind.viewLogin.performClick()
            Assert.assertNotNull(bind.inputViewEmail.error)
            Assert.assertNotNull(bind.inputViewPassword.error)
        }


    }

    @Test
    fun login_input_valid_and_correct_email_password() {
        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            bind.inputViewEmail.text = "quochuy@wee.vn"
            bind.inputViewPassword.text = "concacv1p"
            bind.viewLogin.performClick()
            Assert.assertNotNull(bind.inputViewEmail.error)
            Assert.assertNotNull(bind.inputViewPassword.error)
        }


    }



}