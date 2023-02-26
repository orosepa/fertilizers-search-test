package com.example.ipartner_test.presentation

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ipartner_test.R
import com.example.ipartner_test.databinding.FragmentFertilizersBinding
import com.example.ipartner_test.util.Constants
import com.example.ipartner_test.util.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FertilizersFragment: Fragment(R.layout.fragment_fertilizers) {
    private val viewModel: SearchViewModel by viewModels()
    private val TAG = "FertilizersFragment"
    private lateinit var binding: FragmentFertilizersBinding
    private lateinit var fertilizersAdapter: FertilizersAdapter
    private lateinit var queryTextListener: SearchView.OnQueryTextListener
    private var searchView: SearchView? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFertilizersBinding.inflate(inflater, container, false)
        return binding.root
    }

    var isScrolling = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        viewModel.getAllFertilizers()
        loadData()

        val menuHost: MenuHost = requireActivity()

        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.action_bar_menu, menu)
                val searchItem = menu.findItem(R.id.search)
                val searchManager =
                    requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
                searchView = searchItem.actionView as SearchView?
                searchView?.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
                searchView?.queryHint = "Поиск"

                searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionExpand(p0: MenuItem): Boolean {
                        return true
                    }

                    override fun onMenuItemActionCollapse(p0: MenuItem): Boolean {
                        Log.d(TAG, "Item collapsed!")
                        if (!searchView?.query.isNullOrBlank()) {
                            viewModel.restoreOffset()
                            viewModel.getAllFertilizers()
                        }
                        return true
                    }
                })

                queryTextListener = object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(p0: String?): Boolean {
                        imm.hideSoftInputFromWindow(view.windowToken, 0)
                        return true
                    }

                    override fun onQueryTextChange(p0: String?): Boolean {
                        viewModel.searchFertilizers(p0 ?: "")
                        return true
                    }
                }
                searchView?.setOnQueryTextListener(queryTextListener)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.search -> {
                        menuItem.expandActionView()
                        false
                    }
                    else -> {
                        searchView?.setOnQueryTextListener(queryTextListener)
                        true
                    }
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)


        fertilizersAdapter.setOnItemClickListener { item ->
            val bundle = Bundle().apply {
                putInt("id", item.id)
            }
            findNavController().navigate(
                R.id.action_fertilizersFragment_to_fertilizerCardFragment,
                bundle
            )
        }
    }

    private fun loadData() {
        viewModel.fertilizers.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    binding.rvFertilizers.visibility = View.GONE
                    binding.pbFertilizers.visibility = View.VISIBLE
                }
                is Resource.Error -> {}
                is Resource.Success -> {
                    binding.rvFertilizers.visibility = View.VISIBLE
                    binding.pbFertilizers.visibility = View.GONE
                    val data = response.data
                    fertilizersAdapter.differ.submitList(data?.toMutableList())
                }
            }
        }
    }

    private fun setupRecyclerView() {
        fertilizersAdapter = FertilizersAdapter()
        binding.rvFertilizers.apply {
            adapter = fertilizersAdapter
            layoutManager = GridLayoutManager(activity, 2)
            addOnScrollListener(scrollListener)
        }
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = viewModel.isLastPage.value == false
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.PAGE_SIZE

            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling

            if(shouldPaginate) {
                viewModel.getAllFertilizers()
                isScrolling = false
            } else {
                binding.rvFertilizers.setPadding(0, 0, 0, 0)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }
}