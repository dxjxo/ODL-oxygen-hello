/*
 * Copyright © 2017 dxj and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.odl.hello.impl;

import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev170830.HelloListener;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev170830.StudentPlayTruant;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev170830.StudentsData;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev170830.students.data.Students;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev170830.students.data.StudentsKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloNotificationListener implements HelloListener {
    private static final Logger LOG = LoggerFactory.getLogger(HelloNotificationListener.class);
    private final DataBroker  dataBroker;

    public HelloNotificationListener(DataBroker dataBroker) {
        this.dataBroker = dataBroker;
    }


    @Override
    public void onStudentPlayTruant(StudentPlayTruant notification) {
        WriteTransaction  wt  = dataBroker.newWriteOnlyTransaction();
        InstanceIdentifier<Students> studentsIid = InstanceIdentifier.builder(StudentsData.class)
                .child(Students.class, new StudentsKey(notification.getNumber())).build();
        wt.delete(LogicalDatastoreType.CONFIGURATION, studentsIid);
        try {
            wt.submit().checkedGet();
        } catch (TransactionCommitFailedException e) {
            LOG.error("onStudentPlayTruant error exc:", e);
        }
    }
}
