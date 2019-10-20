package com.hanson.jbpm.jpdl.def.node;


import com.hanson.jbpm.jpdl.def.base.StateException;

public class Start extends NodeBase {
	public Start(String name, StateException exception) {
		super(name, exception);
	}

	public Transition addArrivingTransition(Transition t) {
		throw new UnsupportedOperationException(
				"illegal operation : its not possible to add a transition that is arriving in a start state");
	}
}
