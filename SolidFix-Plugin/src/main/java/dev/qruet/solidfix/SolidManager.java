package dev.qruet.solidfix;

/**
 * A class used to flag classes as a SolidManager type
 * @author qruet
 * @version 2.0_02
 */
public abstract class SolidManager {

    protected SolidManager(CoreManager.Registrar registrar) {
        if (registrar.isRegistered())
            throw new UnsupportedOperationException();
    }

    public abstract boolean disable();

}
