package wee.digital.sample.ui.fragment.plash

import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import wee.digital.sample.databinding.SplashBinding


@RunWith(AndroidJUnit4::class)
class SplashFragmentTest {

    lateinit var bind : SplashBinding

    val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

    val titleScenario = launchFragmentInContainer<SplashFragment>()

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
    }


    @Test
    fun testLogin() {

    }
}