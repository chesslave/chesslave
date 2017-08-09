package io.chesslave.app

import io.chesslave.eyes.BoardAnalyzer
import io.chesslave.eyes.sikuli.SikuliScreen
import io.chesslave.mouth.Utterance
import io.chesslave.mouth.WebSpeechSynthesis

object Chesslave {

    fun configure(events: EventBus) {
        val screen = SikuliScreen()
        val speechSynthesis = WebSpeechSynthesis(events)
        events
            .receive("select-board")
            .flatMap { screen.select("Select board...").map { BoardAnalyzer().analyze(it) } }
            .subscribe(
                {
                    events.send("board-selected")
                    speechSynthesis.speak(Utterance("Scacchiera selezionata"))
                },
                { events.send("board-selection-failed") }
            )
    }
}
