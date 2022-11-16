package triangle.codeGenerator.entities;

import triangle.abstractMachine.Machine;
import triangle.abstractMachine.OpCode;
import triangle.abstractMachine.Primitive;
import triangle.abstractMachine.Register;
import triangle.codeGenerator.Emitter;
import triangle.codeGenerator.Frame;

public class IncrementPrimitiveRoutine extends RuntimeEntity implements RoutineEntity{

	public IncrementPrimitiveRoutine() {
		super(Machine.closureSize);
	}

	@Override
	public void encodeCall(Emitter emitter, Frame frame) {
		emitter.emit(OpCode.LOADL, 0, 1);
		emitter.emit(OpCode.CALL, Register.PB, Primitive.ADD);
	}

	@Override
	public void encodeFetch(Emitter emitter, Frame frame) {
		emitter.emit(OpCode.LOADA, 0, Register.SB, 0);
		emitter.emit(OpCode.LOADA, Register.PB, Primitive.ADD);
	}

}
