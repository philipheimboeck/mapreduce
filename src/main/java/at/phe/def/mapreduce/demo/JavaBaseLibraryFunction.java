package at.phe.def.mapreduce.demo;

import java.util.List;

/**
 * Wrapper of the JavaBaseLibraryFunction that passes the inParameters into the run
 */
public abstract class JavaBaseLibraryFunction extends at.enfilo.def.prototype1.workerapi.JavaBaseLibraryFunction {

    @Override
    public void run() throws Exception {
        run(inParameters);
    }

    public abstract void run(List<String> parameters) throws Exception;
}
