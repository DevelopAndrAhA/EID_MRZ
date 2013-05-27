/*
 * Commons eID Project.
 * Copyright (C) 2008-2013 FedICT.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version
 * 3.0 as published by the Free Software Foundation.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, see 
 * http://www.gnu.org/licenses/.
 */

/**
 * Manual exercise for CardAndTerminalManager.
 * Prints events and list of readers with cards.
 * [short readername] ... 
 * readers with cards inserted have a "*" behind their short name
 * 
 * @author Frank Marien
 * 
 */

package test.integ.be.fedict.commons.eid.client;
import java.util.Random;
import javax.smartcardio.Card;
import javax.smartcardio.CardTerminal;
import org.junit.Test;
import be.fedict.commons.eid.client.CardAndTerminalManager;
import be.fedict.commons.eid.client.event.CardEventsListener;
import be.fedict.commons.eid.client.event.CardTerminalEventsListener;

public class CardAndTerminalManagerExercises
		implements
			CardTerminalEventsListener,
			CardEventsListener {
	private CardAndTerminalManager cardAndTerminalManager;

	// ---------------------------------------------------------------------------------------------

	/*
	 * Exercises asynchronous run with callbacks: instantiate, register
	 * listeners, call start(). The test then loops and adds/removes a listener
	 * in some pseudo-random timing pattern. This is to ensure that the list of
	 * listeners remains properly synchronized in relation to it being iterated
	 * whenever events are being sent to listeners this test never returns..
	 * since it requires someone to attach/detach readers and insert/remove
	 * cards this is no problem until we automate those using e.g.
	 * http://www.lynxmotion
	 * .com/p-816-al5d-robotic-arm-combo-kit-free-software.aspx
	 * 
	 * While running this test, the operator should attach and detach at least 2
	 * card terminals, insert and remove cards from them, in all possible
	 * permutations. The state displayed should, at all times, reflect the state
	 * of the readers and their cards within 250-400 ms.
	 */

	@Test
	public void testDetections() throws Exception {
		final Random random = new Random(0);
		this.cardAndTerminalManager = new CardAndTerminalManager(
				new TestLogger());
		this.cardAndTerminalManager.addCardTerminalListener(this);
		this.cardAndTerminalManager.addCardListener(this);

		// cardAndTerminalManager.ignoreCardEventsFor("VASCO DP905");

		this.cardAndTerminalManager.start();

		final CardTerminalEventsListener dummyCTL = new CardTerminalEventsListener() {

			@Override
			public void terminalDetached(final CardTerminal cardTerminal) {
			}

			@Override
			public void terminalAttached(final CardTerminal cardTerminal) {
			}

			@Override
			public void terminalEventsInitialized() {
				// TODO Auto-generated method stub

			}
		};

		final CardEventsListener dummyCL = new CardEventsListener() {

			@Override
			public void cardRemoved(final CardTerminal cardTerminal) {
			}

			@Override
			public void cardInserted(final CardTerminal cardTerminal,
					final Card card) {
			}

			@Override
			public void cardEventsInitialized() {
				// TODO Auto-generated method stub

			}
		};

		System.err.println("main thread running.. do some card tricks..");

		for (;;) {
			System.err.print("+");
			this.cardAndTerminalManager.addCardTerminalListener(dummyCTL);
			this.cardAndTerminalManager.addCardListener(dummyCL);
			Thread.sleep(random.nextInt(100));
			System.err.print("-");
			this.cardAndTerminalManager.removeCardTerminalListener(dummyCTL);
			this.cardAndTerminalManager.removeCardListener(dummyCL);
			Thread.sleep(random.nextInt(100));
		}
	}

	// ---------------------------------------------------------------------------------------------

	/*
	 * Exercise CardAndTerminalManager's start() stop() semantics, with regards
	 * to its worker thread. This test starts and stops a CardAndTerminalManager
	 * randomly. It should remain in a consistent state at all times and detect
	 * terminal attaches/detaches and card inserts/removals as usual (while
	 * running, of course..)
	 */
	@Test
	public void testStartStop() throws Exception {
		final Random random = new Random(0);
		this.cardAndTerminalManager = new CardAndTerminalManager(
				new TestLogger());
		this.cardAndTerminalManager.addCardTerminalListener(this);
		this.cardAndTerminalManager.addCardListener(this);
		this.cardAndTerminalManager.start();

		for (;;) {
			System.err.print("+");
			this.cardAndTerminalManager.start();
			Thread.sleep(random.nextInt(2000));
			System.err.print("-");
			this.cardAndTerminalManager.stop();
			Thread.sleep(random.nextInt(2000));
		}
	}

	// ----------------------------- callbacks that just print to stderr
	// -------------------

	@Override
	public void terminalAttached(final CardTerminal terminalAttached) {
		System.err.println("Terminal Attached [" + terminalAttached.getName()
				+ "]");
	}

	@Override
	public void terminalDetached(final CardTerminal terminalDetached) {
		System.err.println("Terminal Detached [" + terminalDetached.getName()
				+ "]");
	}

	@Override
	public void cardInserted(final CardTerminal cardTerminal, final Card card) {
		if (card != null) {
			System.err.println("Card ["
					+ StringUtils.atrToString(card.getATR())
					+ "] Inserted Into Terminal [" + cardTerminal.getName()
					+ "]");
		} else {
			System.err.println("Card present but failed to connect()");
		}
	}

	@Override
	public void cardRemoved(final CardTerminal terminalWithCardRemoved) {
		System.err.println("Card Removed From ["
				+ terminalWithCardRemoved.getName() + "]");
	}

	@Override
	public void cardEventsInitialized() {
		System.out.println("Card Events Initialised");

	}

	@Override
	public void terminalEventsInitialized() {
		System.out.println("Terminal Events Initialised");

	}
}
