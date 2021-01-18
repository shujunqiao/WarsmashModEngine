package com.etheller.warsmash.viewer5.handlers.w3x.simulation.behaviors;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.targeting.AbilityTarget;

public abstract class CAbstractRangedBehavior implements CRangedBehavior {
	protected final CUnit unit;
	private final boolean disableCollision;

	public CAbstractRangedBehavior(final CUnit unit, final boolean disableCollision) {
		this.unit = unit;
		this.disableCollision = disableCollision;
	}

	protected AbilityTarget target;
	private boolean wasWithinPropWindow = false;
	private boolean wasInRange = false;
	private CBehaviorMove moveBehavior;

	protected final CAbstractRangedBehavior innerReset(final AbilityTarget target) {
		this.target = target;
		this.wasWithinPropWindow = false;
		this.wasInRange = false;
		CBehaviorMove moveBehavior;
		if (!this.unit.isMovementDisabled()) {
			moveBehavior = this.unit.getMoveBehavior().reset(this.target, this, this.disableCollision);
		}
		else {
			moveBehavior = null;
		}
		this.moveBehavior = moveBehavior;
		return this;
	}

	protected abstract CBehavior update(CSimulation simulation, boolean withinRange);

	protected abstract boolean checkTargetStillValid(CSimulation simulation);

	protected abstract void resetBeforeMoving(CSimulation simulation);

	@Override
	public final CBehavior update(final CSimulation simulation) {
		if (!checkTargetStillValid(simulation)) {
			return this.unit.pollNextOrderBehavior(simulation);
		}
		if (!isWithinRange(simulation)) {
			if (this.moveBehavior == null) {
				return this.unit.pollNextOrderBehavior(simulation);
			}
			this.wasInRange = false;
			resetBeforeMoving(simulation);
			return this.unit.getMoveBehavior();
		}
		this.wasInRange = true;
		if (!this.unit.isMovementDisabled()) {
			final float prevX = this.unit.getX();
			final float prevY = this.unit.getY();
			final float deltaX = this.target.getX() - prevX;
			final float deltaY = this.target.getY() - prevY;
			final double goalAngleRad = Math.atan2(deltaY, deltaX);
			float goalAngle = (float) Math.toDegrees(goalAngleRad);
			if (goalAngle < 0) {
				goalAngle += 360;
			}
			float facing = this.unit.getFacing();
			float delta = goalAngle - facing;
			final float propulsionWindow = simulation.getGameplayConstants().getAttackHalfAngle();
			final float turnRate = simulation.getUnitData().getTurnRate(this.unit.getTypeId());

			if (delta < -180) {
				delta = 360 + delta;
			}
			if (delta > 180) {
				delta = -360 + delta;
			}
			final float absDelta = Math.abs(delta);

			if ((absDelta <= 1.0) && (absDelta != 0)) {
				this.unit.setFacing(goalAngle);
			}
			else {
				float angleToAdd = Math.signum(delta) * (float) Math.toDegrees(turnRate);
				if (absDelta < Math.abs(angleToAdd)) {
					angleToAdd = delta;
				}
				facing += angleToAdd;
				this.unit.setFacing(facing);
			}
			if (absDelta < propulsionWindow) {
				this.wasWithinPropWindow = true;
			}
			else {
				// If this happens, the unit is facing the wrong way, and has to turn before
				// moving.
				this.wasWithinPropWindow = false;
			}
		}
		else {
			this.wasWithinPropWindow = true;
		}

		return update(simulation, this.wasWithinPropWindow);
	}

}
