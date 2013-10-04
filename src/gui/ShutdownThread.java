package gui;


class ShutdownThread implements Runnable {
	MainFrame parent;

	public ShutdownThread(MainFrame parent) {
		this.parent = parent;
	}

	@Override
	public void run() {
		parent.exit();
	}

}