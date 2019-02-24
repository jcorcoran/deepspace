package frc.team2767.deepspace.command.states;

import edu.wpi.first.wpilibj.command.InstantCommand;
import frc.team2767.deepspace.Robot;
import frc.team2767.deepspace.subsystem.BiscuitSubsystem;
import frc.team2767.deepspace.subsystem.FieldDirection;
import frc.team2767.deepspace.subsystem.VisionSubsystem;

public class SetFieldDirectionCommand extends InstantCommand {

  private static final BiscuitSubsystem BISCUIT = Robot.BISCUIT;
  private static final VisionSubsystem VISION = Robot.VISION;
  private FieldDirection direction;

  public SetFieldDirectionCommand(FieldDirection direction) {
    this.direction = direction;
    requires(BISCUIT);
    requires(VISION);
  }

  @Override
  protected void initialize() {
    BISCUIT.setFieldDirection(direction);
    VISION.setFieldDirection(direction);
  }
}