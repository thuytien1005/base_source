package wee.digital.sample.ui.fragment.plash

import com.google.firebase.auth.FirebaseUser
import org.junit.After
import org.junit.Before
import org.junit.Test
import wee.digital.sample.App
import wee.digital.sample.R
import kotlin.test.assertEquals


class SplashVMTest {

    private val app = App()
    private val vm = SplashVM()
    var user: FirebaseUser? = null

    @Before
    fun setUp() {
        user = null
        app.onCreate()


    }

    @After
    fun tearDown() {
    }

    @Test
    fun testUserLogin() {
        assertEquals(vm.nextDestination, R.id.action_global_homeFragment)
    }

    @Test
    fun testUserLogout() {
        assertEquals(vm.nextDestination, R.id.action_global_homeFragment)
    }
}