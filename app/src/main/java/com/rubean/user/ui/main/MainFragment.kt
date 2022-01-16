package com.rubean.user.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.rubean.user.R
import com.rubean.user.dummywordgame.DummyWordGameModerator
import com.rubean.user.dummywordgame.MoveResult
import com.rubean.user.dummywordgame.MoveStatus
import com.rubean.user.extensions.clickWithDebounce
import com.rubean.user.extensions.hideKeyboard

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var progressBar: ProgressBar
    private lateinit var buttonSendWord: MaterialButton

    private val dummyWordGameModerator = DummyWordGameModerator(
        humanWon = {
            requireActivity().hideKeyboard()
            hideProgressAndEnableUserInteraction()
            Toast.makeText(requireContext(),  getEndOfTheGameMessage(hasTheHumanWon = true, result = it), Toast.LENGTH_LONG).show()
        },
        botWon = {
            requireActivity().hideKeyboard()
            hideProgressAndEnableUserInteraction()
            Toast.makeText(requireContext(), getEndOfTheGameMessage(hasTheHumanWon = false, result = it), Toast.LENGTH_LONG).show()
        },
        botHasMoved = {
            hideProgressAndEnableUserInteraction()
            updateAdapter()
        },
        draw = {
            requireActivity().hideKeyboard()
            hideProgressAndEnableUserInteraction()
            Toast.makeText(requireContext(), R.string.you_draw, Toast.LENGTH_LONG).show()
        })

    private val wordsAdapter = WordsRecyclerViewAdapter(emptyList())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews(view)
    }

    private fun setupViews(view: View) {
        progressBar = view.findViewById(R.id.progressBar)
        buttonSendWord = view.findViewById(R.id.buttonSendWord)

        with(view.findViewById<EditText>(R.id.editTextSpellWords)) {
            setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    handleUserInput(this)
                    true
                } else {
                    false
                }
            }

            buttonSendWord.clickWithDebounce {
                handleUserInput(this)
            }
        }

        with(view.findViewById<RecyclerView>(R.id.recyclerViewWords)) {
            adapter = wordsAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun handleUserInput(editText: EditText) {
        showProgressAndPreventUserInteraction()
        val text = editText.text.toString()
        if (!isInputtedTextValid(text)) {
            Toast.makeText(requireContext(), R.string.error_invalid_text, Toast.LENGTH_SHORT).show()
            return
        }
        dummyWordGameModerator.handleHumanPlayerInput(text)
        updateAdapter()
        editText.text.clear()
    }

    private fun showProgressAndPreventUserInteraction() {
        progressBar.isVisible = true
        buttonSendWord.isVisible = false
    }

    private fun hideProgressAndEnableUserInteraction() {
        progressBar.isVisible = false
        buttonSendWord.isVisible = true
    }

    private fun updateAdapter() {
        wordsAdapter.words = dummyWordGameModerator.previouslyPlayedWords
        wordsAdapter.notifyDataSetChanged()
    }

    private fun isInputtedTextValid(text: String): Boolean {
        return text.trim().isNotEmpty()
    }

    private fun getEndOfTheGameMessage(hasTheHumanWon: Boolean, result: MoveResult): String {
        val message = if (hasTheHumanWon) {
            getString(R.string.you_won)
        } else {
            getString(R.string.you_lost)
        }

        val reason = when (result.status) {
            MoveStatus.ERROR_MORE_THAN_ONE_WORD -> getString(R.string.end_game_reason_more_than_one_word)
            MoveStatus.ERROR_REPEATED_WORD -> getString(
                R.string.end_game_reason_repeated_word,
                result.errorCause[0]
            )
            MoveStatus.ERROR_MISSTYPED -> getString(
                R.string.end_game_reason_misstype,
                result.errorCause[0],
                result.errorCause[1]
            )
            MoveStatus.SUCCESS -> ""
        }

        return "$message\n$reason"
    }

    override fun onStart() {
        super.onStart()
        dummyWordGameModerator.start(requireActivity())
    }

    override fun onStop() {
        dummyWordGameModerator.stop(requireActivity())
        super.onStop()
    }
}