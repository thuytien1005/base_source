package wee.digital.sample

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import wee.digital.sample.ui.fragment.plash.SplashVM
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class SplashVMTest {

    private val dispatcher = TestCoroutineDispatcher()
    private val coroutineScope = TestCoroutineScope(dispatcher)
    private val vm = SplashVM()

    @Before
    fun setup() {
        //FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext())
        //Dispatchers.setMain(dispatcher)
    }

    @Test
    fun `navigate that has auth user`() {


    }



}