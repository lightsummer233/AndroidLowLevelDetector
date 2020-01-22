package net.imknown.android.forefrontinfo.ui.others

import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import net.imknown.android.forefrontinfo.ui.base.BaseListFragment

class OthersFragment : BaseListFragment() {

    companion object {
        fun newInstance() = OthersFragment()
    }

    override val listViewModel by activityViewModels<OthersViewModel>()

    override fun init() {
        listViewModel.rawProp.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {
                listViewModel.collectModels()
            }
        })
    }
}