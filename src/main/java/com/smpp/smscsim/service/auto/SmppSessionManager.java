package com.smpp.smscsim.service.auto;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppServerSession;

/**
 * Manages all RX and TRX bound SMPP connections represented by session instance.
 * These connections are kept in global list and separate list for each
 * distinct application id.
 * Class is thread safe.
 * When SMSCsim wants to send out a MO message, it requests a session by calling getNextServerSession. A session is
 * chosen based on round robin algorithm.
 **/
@Component
public class SmppSessionManager {

	private static final Logger logger = LoggerFactory.getLogger(SmppSessionManager.class);

	private final Map<String, SessionList> applicationSessionMap;

	private final AtomicInteger sequenceNumber = new AtomicInteger(0);

	private final SessionList globalSessionList = new SessionList();

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	public SmppSessionManager() {
		applicationSessionMap = new ConcurrentHashMap<>();
	}

	public void addServerSession(SmppServerSession session) {
		if (session.getBindType() == SmppBindType.TRANSMITTER) {
			// we ignore transmitters as they can not be used to send delivers
			return;
		}

		lock.writeLock().lock();
		try {
			String systemId = session.getConfiguration().getSystemId();
			SessionList sessionList = applicationSessionMap.get(systemId);
			if (sessionList == null) {
				sessionList = new SessionList();
				applicationSessionMap.put(systemId, sessionList);
			}
			sessionList.add(session);
			globalSessionList.add(session);
		} finally {
			lock.writeLock().unlock();
		}
	}

	public void removeServerSession(SmppServerSession session) {
		if (session.getBindType() == SmppBindType.TRANSMITTER) {
			// we ignore transmitters as they can not be used to send delivers
			return;
		}

		lock.writeLock().lock();
		try {
			String systemId = session.getConfiguration().getSystemId();
			SessionList sessionList = applicationSessionMap.get(systemId);
			boolean removed = false;
			if (sessionList != null) {
				removed = sessionList.remove(session);
				globalSessionList.remove(session);
			}
			if (!removed) {
				logger.warn("Failed to remove session %s, Session not found.", session);
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	public SmppServerSession getNextServerSession(String appSystemId) {
		lock.readLock().lock();
		try {
			SessionList sessionList = applicationSessionMap.get(appSystemId);
			if (sessionList != null) {
				return sessionList.getNext();
			}
			return null;
		} finally {
			lock.readLock().unlock();
		}
	}

	public SmppServerSession getNextServerSession() {
		lock.readLock().lock();
		try {
			return globalSessionList.getNext();
		} finally {
			lock.readLock().unlock();
		}
	}

	public int getSessionCount() {
		lock.readLock().lock();
		try {
			return globalSessionList.size();
		} finally {
			lock.readLock().unlock();
		}
	}

	public int getNextSequenceNumber() {
		return sequenceNumber.incrementAndGet();
	}

    /**
     * Internal session list using CopyOnWriteArrayList with lock-free round-robin.
     **/
	private static final class SessionList {
		private final CopyOnWriteArrayList<SmppServerSession> sessions = new CopyOnWriteArrayList<>();
		private final AtomicInteger counter = new AtomicInteger(0);

		public void add(SmppServerSession session) {
			sessions.add(session);
		}

		public boolean remove(Object o) {
			return sessions.remove(o);
		}

		public int size() {
			return sessions.size();
		}

		public SmppServerSession getNext() {
			List<SmppServerSession> snapshot = sessions;
			int size = snapshot.size();
			if (size == 0) {
				return null;
			}
			int index = Math.abs(counter.getAndIncrement() % size);
			return snapshot.get(index);
		}
	}
}
