package org.activiti.test;

import org.activiti.engine.impl.db.DbSchemaCreate;
import org.junit.Test;

public class InitDB {
    /**
     * Add the activiti-engine jars to your classpath
     * Add a suitable database driver
     * Add an Activiti configuration file (activiti.cfg.xml) to your classpath, pointing to your database (see database configuration section)
     * Execute the main method of the DbSchemaCreate class
     */
    @Test
    public void createSchemaTest() {
        DbSchemaCreate.main(null);
    }
}
