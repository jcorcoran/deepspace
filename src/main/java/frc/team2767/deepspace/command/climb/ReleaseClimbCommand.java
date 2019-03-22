package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.Command;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.ClimbSubsystem;

public class ReleaseClimbCommand extends Command {
  // Runs Down 4000 ticks to pull pin
  // Disables ratchet after going at least 50 ticks

  private static final ClimbSubsystem CLIMB = Robot.CLIMB;

  public ReleaseClimbCommand() {
    requires(CLIMB);
  }

  @Override
  protected void initialize() {
    CLIMB.runStringPot(ClimbSubsystem.kLowReleaseIn);
  }

  @Override
  protected boolean isFinished() {
    return CLIMB.onStringPot();
  }

  @Override
  protected void end() {
    CLIMB.stop();
  }
}
