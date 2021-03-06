package io.chesslave.eyes

import io.chesslave.app.log
import io.chesslave.eyes.sikuli.SikuliVision
import io.chesslave.model.Color
import io.chesslave.model.Game
import io.chesslave.visual.Images
import io.chesslave.visual.model.BoardImage
import io.reactivex.Observable
import io.reactivex.rxkotlin.zipWith
import java.awt.Rectangle
import java.util.concurrent.TimeUnit

class BoardObserver(val config: BoardConfiguration, val screen: Screen) {

    private val vision: Vision = SikuliVision()
    private val recogniser: GameRecogniser = GameRecogniser(
        PositionRecogniser(vision, config),
        MoveRecogniserByImageDiff(PieceRecogniser(vision, config)),
        MoveRecogniserByPositionDiff())

    fun start(color: Color): Observable<Game> {
        // detecting initial position
        val match = findBoardInDesktop(config.board)
        val initImage = BoardImage(match.image(), match.region().location)
        val initGame = recogniser.begin(initImage, color)
        log.debug("initial position:\n${initGame.position()}")
        // following game
        val boards = captureBoards(match.region())
        return observeGame(initGame, boards)
    }

    private fun findBoardInDesktop(board: BoardImage): Vision.Match {
        val desktop = screen.captureAll()
        val match = vision.recognise(desktop).bestMatch(board.image).get()
        screen.highlight(match.region(), 1, TimeUnit.SECONDS)
        return match
    }

    private fun captureBoards(region: Rectangle): Observable<Pair<BoardImage, BoardImage>> {
        val boards = Observable.interval(1, TimeUnit.SECONDS)
            .map { BoardImage(screen.capture(region), region.location) }
        return boards.zipWith(boards.skip(1)) { a, b -> Pair(a, b) }
            .filter { Images.areDifferent(it.first.image, it.second.image) }
    }

    private fun observeGame(initGame: Game, boards: Observable<Pair<BoardImage, BoardImage>>): Observable<Game> =
        boards.scan(initGame) { game, (previous, current) ->
            // TODO: restore from eventual exception
            val move = recogniser.next(game, previous, current)
            if (move == null) {
                log.debug("nothing is changed")
                game
            } else {
                // TODO: validate move
                log.debug("detective move:\n$move")
                game.move(move)
            }
        }
}