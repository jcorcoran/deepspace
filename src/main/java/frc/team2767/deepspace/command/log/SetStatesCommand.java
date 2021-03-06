package frc.team2767.deepspace.command.log;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.*;

public class SetStatesCommand extends InstantCommand {

  private static final VisionSubsystem VISION = Robot.VISION;

  private ElevatorLevel level;
  private GamePiece gamePiece;
  private Action action;

  public SetStatesCommand(GamePiece gamePiece) {
    this(null, gamePiece, null);
  }

  public SetStatesCommand(ElevatorLevel level, GamePiece gamePiece, Action action) {
    this.level = level;
    this.gamePiece = gamePiece;
    this.action = action;

    requires(VISION);
  }

  public SetStatesCommand(Action action) {
    this(ElevatorLevel.NOTSET, GamePiece.NOTSET, action);
  }

  public SetStatesCommand(ElevatorLevel level) {
    this(level, GamePiece.NOTSET, Action.NOTSET);
  }

  public SetStatesCommand(ElevatorLevel level, Action action) {
    this(level, GamePiece.NOTSET, action);
  }

  public SetStatesCommand(GamePiece gamePiece, Action action) {
    this(ElevatorLevel.NOTSET, gamePiece, action);
  }

  public SetStatesCommand(ElevatorLevel level, GamePiece gamePiece) {
    this(level, gamePiece, Action.NOTSET);
  }

  @Override
  protected void initialize() {
    VISION.setElevatorLevel(level);
    VISION.setGamePiece(gamePiece);
    VISION.setAction(action);
  }
}
