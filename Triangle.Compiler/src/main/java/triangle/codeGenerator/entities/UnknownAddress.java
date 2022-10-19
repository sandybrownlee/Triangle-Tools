/*
 * @(#)UnknownAddress.java                        2.1 2003/10/07
 *
 * Copyright (C) 1999, 2003 D.A. Watt and D.F. Brown
 * Dept. of Computing Science, University of Glasgow, Glasgow G12 8QQ Scotland
 * and School of Computer and Math Sciences, The Robert Gordon University,
 * St. Andrew Street, Aberdeen AB25 1HG, Scotland.
 * All rights reserved.
 *
 * This software is provided free for educational use only. It may
 * not be used for commercial purposes without the prior written permission
 * of the authors.
 */

package triangle.codeGenerator.entities;

import triangle.abstractMachine.Machine;
import triangle.abstractMachine.OpCode;
import triangle.abstractMachine.Primitive;
import triangle.abstractMachine.Register;
import triangle.abstractSyntaxTrees.vnames.Vname;
import triangle.codeGenerator.Emitter;
import triangle.codeGenerator.Frame;

public class UnknownAddress extends AddressableEntity {

	public UnknownAddress(int size, int level, int displacement) {
		super(size, level, displacement);
	}

	// In this case a routine has been called with a variable as one of its parameters.
	// That means that an address for where that variable can be found in memory was placed on the stack before the routine was called.
	// address pointing to the part of the stack where the variable can be found
	// Size is the amount of extra space needed to hold a variable
	public void encodeStore(Emitter emitter, Frame frame, int size, Vname vname) {

		emitter.emit(OpCode.LOAD, Machine.addressSize, frame.getDisplayRegister(address), address.getDisplacement()); //first load the address of the routine’s frame using LOAD
		if (vname.indexed) {
			emitter.emit(OpCode.CALL, Register.PB, Primitive.ADD);
		}

		int offset = vname.offset;
		if (offset != 0) {
			emitter.emit(OpCode.LOADL, 0, offset); // jump back up the stack to the arguments, using LOADL and ADD
			emitter.emit(OpCode.CALL, Register.PB, Primitive.ADD); // ADD adds a negative number to the location of the base of the stack (where the argument is located)
		}
		emitter.emit(OpCode.STOREI, size, 0); //access the value in variable's true location
	}

	public void encodeFetch(Emitter emitter, Frame frame, int size, Vname vname) {
		emitter.emit(OpCode.LOAD, Machine.addressSize, frame.getDisplayRegister(address), address.getDisplacement());

		if (vname.indexed) {
			emitter.emit(OpCode.CALL, Register.PB, Primitive.ADD);
		}

		int offset = vname.offset;
		if (offset != 0) {
			emitter.emit(OpCode.LOADL, offset);
			emitter.emit(OpCode.CALL, Register.PB, Primitive.ADD);
		}
		emitter.emit(OpCode.LOADI, size);
	}

	public void encodeFetchAddress(Emitter emitter, Frame frame, Vname vname) {

		emitter.emit(OpCode.LOAD, Machine.addressSize, frame.getDisplayRegister(address), address.getDisplacement());
		if (vname.indexed) {
			emitter.emit(OpCode.CALL, Register.PB, Primitive.ADD);
		}

		int offset = vname.offset;
		if (offset != 0) {
			emitter.emit(OpCode.LOADL, offset);
			emitter.emit(OpCode.CALL, Register.PB, Primitive.ADD);
		}
	}
}