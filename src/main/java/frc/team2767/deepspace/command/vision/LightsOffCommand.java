package frc.team2767.deepspace.command.vision;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class LightsOffCommand extends InstantCommand {

  private static final VisionSubsystem VISION = Robot.VISION;

  public LightsOffCommand() {
    setRunWhenDisabled(true);
    requires(VISION);
  }

  @Override
  protected void initialize() {
    VISION.enableLights(false);
  }
}
