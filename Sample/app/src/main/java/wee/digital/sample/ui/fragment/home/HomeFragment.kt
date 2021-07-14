package wee.digital.sample.ui.fragment.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import wee.digital.library.adapter.FragmentAdapter
import wee.digital.sample.R
import wee.digital.sample.databinding.HomeBinding
import wee.digital.sample.ui.fragment.contact.ContactFragment
import wee.digital.sample.ui.fragment.me.MeFragment
import wee.digital.sample.ui.main.MainFragment
import wee.digital.sample.utils.homeNavItem
import wee.digital.widget.extension.color

class HomeFragment : MainFragment<HomeBinding>() {

    /**
     * [MainFragment] implements
     */
    override fun inflating(): (LayoutInflater) -> HomeBinding {
        return HomeBinding::inflate
    }

    override fun onViewCreated() {
        addClickListener()
        configViewPager()
        configBottomNavigation()
    }

    override fun onLiveDataObserve() {
        userVM.firebaseUserLiveData.observe {
            when (it) {
                null -> navigateLogin()
            }
        }
    }

    override fun onViewClick(v: View?) {
        when (v) {

        }
    }

    /**
     * [HomeFragment] properties
     */
    private fun configBottomNavigation() {
        bind.bottomNavigation.also {
            it.defaultBackgroundColor = Color.WHITE
            it.isBehaviorTranslationEnabled = true
            it.titleState = AHBottomNavigation.TitleState.ALWAYS_HIDE
            it.inactiveColor = Color.parseColor("#A1A1A1")
            it.accentColor = color(R.color.colorPrimary)
            it.setNotificationBackgroundColor(Color.parseColor("#F04541"))
            it.addItem(homeNavItem(R.drawable.ic_placeholder))
            it.addItem(homeNavItem(R.drawable.ic_placeholder))
            it.currentItem = 0
            it.setOnTabSelectedListener { position: Int, wasSelected: Boolean ->
                if (!wasSelected) {
                    bind.viewPager.setCurrentItem(position, false)
                }
                true
            }
        }

    }

    private fun configViewPager() {
        val adapter = FragmentAdapter(this).also {
            it.addFragments(ContactFragment(), MeFragment())
        }
        bind.viewPager.also {
            it.offscreenPageLimit = 4
            it.adapter = adapter
        }
    }

    private fun navigateLogin() {
        navigate(R.id.action_global_loginFragment) {
            setLaunchSingleTop()
        }
    }
}