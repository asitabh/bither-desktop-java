package net.bither.db;

import net.bither.bitherj.db.AbstractDb;
import net.bither.preference.UserPreference;

import java.sql.*;

public class AddressDBHelper extends AbstractDBHelper {

    private static final String DB_NAME = "address.db";
    private static final int CURRENT_VERSION = 2;

    public AddressDBHelper(String dbDir) {
        super(dbDir);
    }

    @Override
    protected String getDBName() {
        return DB_NAME;
    }

    @Override
    protected int currentVersion() {
        return CURRENT_VERSION;
    }

    @Override
    protected int dbVersion() {
        int dbVersion = UserPreference.getInstance().getAddressDbVersion();
        if (dbVersion == 0) {
            //no record dbversion is 1
            try {
                Connection connection = getConn();
                assert connection != null;
                if (hasAddressTables(connection)) {
                    dbVersion = 1;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return dbVersion;
    }

    @Override
    protected void onUpgrade(Connection conn, int newVersion, int oldVerion) throws SQLException {
        Statement stmt = conn.createStatement();
        switch (oldVerion) {
            case 1:
                v1Tov2(stmt);
        }
        conn.commit();
        UserPreference.getInstance().setAddressDbVersion(CURRENT_VERSION);
    }

    @Override
    protected void onCreate(Connection conn) throws SQLException {

        if (hasAddressTables(conn)) {
            return;
        }
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(AbstractDb.CREATE_ADDRESSES_SQL);
        stmt.executeUpdate(AbstractDb.CREATE_HDM_BID_SQL);
        stmt.executeUpdate(AbstractDb.CREATE_HD_SEEDS_SQL);
        stmt.executeUpdate(AbstractDb.CREATE_HDM_ADDRESSES_SQL);
        stmt.executeUpdate(AbstractDb.CREATE_PASSWORD_SEED_SQL);
        stmt.executeUpdate(AbstractDb.CREATE_ALIASES_SQL);

        stmt.executeUpdate(AbstractDb.CREATE_HD_ACCOUNT);
        conn.commit();
        UserPreference.getInstance().setAddressDbVersion(CURRENT_VERSION);

    }

    private void v1Tov2(Statement statement) throws SQLException {
        statement.executeUpdate(AbstractDb.CREATE_HD_ACCOUNT);

    }

    private boolean hasAddressTables(Connection conn) throws SQLException {
        ResultSet rs = conn.getMetaData().getTables(null, null, AbstractDb.Tables.Addresses, null);
        boolean hasTable = rs.next();
        rs.close();
        return hasTable;

    }


}
