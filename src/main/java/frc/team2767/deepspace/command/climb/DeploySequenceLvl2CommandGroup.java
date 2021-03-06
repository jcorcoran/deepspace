package frc.team2767.deepspace.command.climb;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.team2767.deepspace.command.elevator.ElevatorSetPositionCommand;
import frc.team2767.deepspace.command.log.LogCommand;
import frc.team2767.deepspace.command.teleop.StowElevatorConditionalCommand;
import frc.team2767.deepspace.command.vacuum.PressureSetCommand;
import frc.team2767.deepspace.command.vacuum.SetSolenoidStatesCommand;
import frc.team2767.deepspace.subsystem.VacuumSubsystem;

public class DeploySequenceLvl2CommandGroup extends CommandGroup {
  public DeploySequenceLvl2CommandGroup() {
    addSequential(new LogCommand("BEGIN DEPLOY SEQUENCE"));
    addSequential(
        new CommandGroup() {
          {
            addSequential(new PressureSetCommand(VacuumSubsystem.kClimbPressureInHg), 0.3);
            addSequential(new SetSolenoidStatesCommand(VacuumSubsystem.SolenoidStates.CLIMB));
            addParallel(
                new CommandGroup() {
                  {
                    addSequential(new StowElevatorConditionalCommand());
                    addSequential(new ElevatorSetPositionCommand(5.0), 0.5);
                  }
                });
            addParallel(new EngageRatchetCommand(false));
            addParallel(new ClimbLevel2DeployCommand());
          }
        });
    addSequential(new LogCommand("END DEPLOY SEQUENCE"));
  }
}
