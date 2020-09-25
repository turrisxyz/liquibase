package liquibase.change.core

import liquibase.change.StandardChangeTest
import liquibase.database.core.OracleDatabase
import liquibase.parser.core.ParsedNode
import liquibase.database.core.MockDatabase
import liquibase.sdk.resource.MockResourceAccessor
import liquibase.snapshot.MockSnapshotGeneratorFactory
import liquibase.snapshot.SnapshotGeneratorFactory

public class CreateProcedureChangeTest extends StandardChangeTest {

    def getConfirmationMessage() throws Exception {
        when:
        CreateProcedureChange refactoring = new CreateProcedureChange();

        then:
        "Stored procedure created" == refactoring.getConfirmationMessage()
    }

    def "checkStatus"() {
        when:
        def database = new MockDatabase()
        def snapshotFactory = new MockSnapshotGeneratorFactory()
        SnapshotGeneratorFactory.instance = snapshotFactory
        def change = new CreateProcedureChange()
        change.procedureName = "test_proc"

        then:
        assert change.checkStatus(database).message == "Cannot check createProcedure status"
    }

    def "load with inline sql"() {
        when:
        def change = new CreateProcedureChange()
        change.load(new ParsedNode(null, "createProcedure").setValue("create procedure sql"), new MockResourceAccessor())
        change.validate(new OracleDatabase())

        then:
        change.serialize().toString() == "createProcedure[procedureBody=create procedure sql]"
    }

    def "openStream"() {
        when:
        def change = new CreateProcedureChange();
        change.path = "liquibase/change/core/SQLFileTestData.sql"
        change.resourceAccessor = resourceSupplier.simpleResourceAccessor

        def is = change.openStream()

        then:
        is.close()
        assert is != null
    }
}
