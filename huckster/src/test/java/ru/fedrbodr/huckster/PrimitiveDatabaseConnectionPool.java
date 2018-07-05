package ru.fedrbodr.huckster;

import java.util.Arrays;
import java.util.Objects;

public class PrimitiveDatabaseConnectionPool {
	private Connection[] freeConnections;
	private Connection[] connectionsInWork;

	public PrimitiveDatabaseConnectionPool(int connectionsCount) {
		connectionsInWork = new Connection[connectionsCount];
		this.freeConnections = new Connection[connectionsCount];
		int arrayLength = connectionsCount - 1;
		while (arrayLength > -1) {
			this.freeConnections[arrayLength] = new Connection(arrayLength);
			arrayLength--;
		}
	}

	public Connection getConnection() {
		/*TODO if realise! must return not null */
		Connection connection = null;
		if(freeConnections.length>0) {
			Connection finalConnection = this.freeConnections[this.freeConnections.length-1];
			this.freeConnections = Arrays.stream(freeConnections)
					.filter(e -> !e.equals(finalConnection)).toArray(Connection[]::new);
			connectionsInWork[connectionsInWork.length-1] = finalConnection;
			connection = finalConnection;
		}else{
			//"Not found free connection in connection pool"
			// wait and then get first open connection if need by logic
		}

		return connection;
	}

	private class Connection {
		int id;

		public Connection(int id) {
			this.id = id;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Connection)) return false;
			Connection that = (Connection) o;
			return id == that.id;
		}

		@Override
		public int hashCode() {
			return Objects.hash(id);
		}

		@Override
		public String toString() {
			return "Connection{" +
					"id=" + id +
					'}';
		}
	}
}
