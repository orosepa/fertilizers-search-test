package com.example.ipartner_test.presentation

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.ipartner_test.R
import com.example.ipartner_test.databinding.FragmentFertilizerCardBinding
import com.example.ipartner_test.domain.models.IPartnerResponse
import com.example.ipartner_test.util.Constants
import com.example.ipartner_test.util.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class FertilizerCardFragment : Fragment(R.layout.fragment_fertilizer_card) {

    private lateinit var binding: FragmentFertilizerCardBinding
    private var fertilizer: IPartnerResponse? = null
    private val args: FertilizerCardFragmentArgs by navArgs()
    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFertilizerCardBinding.inflate(inflater, container, false)
        viewModel.getFertilizerById(args.id)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.currentFertilizer.observe(viewLifecycleOwner) { response ->
            Log.d("FertilizerCard", "${response.data ?: response.message}")
            when (response) {
                is Resource.Success -> {
                    fertilizer = response.data
                    Glide.with(this)
                        .load(Constants.BASE_URL + fertilizer?.image)
                        .into(binding.singleItemImage)
                    Glide.with(this)
                        .load(Constants.BASE_URL + fertilizer?.categories?.icon)
                        .into(binding.singleItemIcon)

                    binding.singleFertilizerTitle.text = fertilizer?.name
                    binding.singleFertilizerDescription.text = fertilizer?.description
                }
                is Resource.Loading -> {}
                is Resource.Error -> {}
            }

        }
    }
}