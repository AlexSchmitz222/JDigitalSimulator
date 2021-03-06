/*
 * JDigitalSimulator
 * Copyright (C) 2017 Kristian Kraljic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package lc.kra.jds.components.buildin.register;

import static lc.kra.jds.Utilities.*;

import java.awt.Graphics;
import java.awt.Point;
import java.beans.PropertyVetoException;
import java.util.Map;

import lc.kra.jds.Utilities.TranslationType;
import lc.kra.jds.contacts.Contact;
import lc.kra.jds.contacts.ContactUtilities;
import lc.kra.jds.contacts.InputContact;
import lc.kra.jds.contacts.OutputContact;

/**
 * Shift-Register (build-in component)
 * @author Kristian Kraljic (kris@kra.lc)
 */
public class ShiftRegister extends Register {
	private static final long serialVersionUID = 2l;

	private static final String KEY;
	static { KEY = "component.register."+ShiftRegister.class.getSimpleName().toLowerCase(); }
	public static final ComponentAttributes componentAttributes = new ComponentAttributes(KEY, getTranslation(KEY), "group.register", getTranslation(KEY, TranslationType.DESCRIPTION), "Kristian Kraljic (kris@kra.lc)", 1);

	protected InputContact inputI, inputC, inputR;

	private Contact[] contacts;

	protected boolean oldClock;

	public ShiftRegister() {
		inputI = new InputContact(this, new Point(0, 6));
		inputC = new InputContact(this, new Point(0, 16));
		inputR = new InputContact(this, new Point(0, 26));

		contacts = ContactUtilities.concatenateContacts(new Contact[]{inputI, inputC, inputR}, outputs.toArray());
		this.setContactLocations(outputs);
	}

	@Override public void paint(Graphics graphics) {
		super.paint(graphics);
		graphics.drawString("+", 8, inputI.getLocation().y+4);
		graphics.drawString("C", 8, inputC.getLocation().y+4);
		graphics.drawString("R", 8, inputR.getLocation().y+4);
		graphics.setFont(graphics.getFont().deriveFont(10f));
		graphics.drawString("Shift", 20, 14);
		ContactUtilities.paintSolderingJoints(graphics, contacts);
	}

	@Override public Contact[] getContacts() { return contacts; }
	@Override public void calculate() {
		boolean clock = inputC.isCharged();
		if(inputR.isCharged()) {
			for(OutputContact output:outputs)
				output.setCharged(false);
		} else if(!oldClock&&clock) {
			for(int output=outputs.getContactsCount()-1;output>0;output--)
				outputs.getContact(output).setCharged(outputs.getContact(output-1).isCharged());
			outputs.getContact(0).setCharged(inputI.isCharged());
		}
		oldClock = clock;
	}

	@Override public void setConfiguration(Map<Option, Object> configuration) throws PropertyVetoException {
		super.setConfiguration(configuration);
		contacts = ContactUtilities.concatenateContacts(new Contact[]{inputI, inputC, inputR}, outputs.toArray());
	}
}