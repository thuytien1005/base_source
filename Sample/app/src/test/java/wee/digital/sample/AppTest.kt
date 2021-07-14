package wee.digital.sample

import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull

class AppTest {

    private val app = App()

    @Before
    fun setUp() {
        app.onCreate()
    }

    @After
    fun tearDown() {
    }

    @Test
    fun test_modules_init() {
        assertNotNull(wee.digital.library.app)
        assertNotNull(wee.digital.widget.app)
    }

}