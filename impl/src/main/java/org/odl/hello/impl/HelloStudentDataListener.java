/*
 * Copyright © 2017 dxj and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */


package org.odl.hello.impl;
import java.util.Collection;
import javax.annotation.Nonnull;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.DataObjectModification;
import org.opendaylight.controller.md.sal.binding.api.DataTreeChangeListener;
import org.opendaylight.controller.md.sal.binding.api.DataTreeIdentifier;
import org.opendaylight.controller.md.sal.binding.api.DataTreeModification;
import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev170830.StudentsData;
import org.opendaylight.yangtools.concepts.ListenerRegistration;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class HelloStudentDataListener implements AutoCloseable, DataTreeChangeListener<StudentsData> {
    private static final Logger LOG = LoggerFactory.getLogger(HelloStudentDataListener.class);
    private ListenerRegistration listenerRegistration;
    private final DataBroker  dataBroker;

    public HelloStudentDataListener(DataBroker dataBroker) {
        this.dataBroker = dataBroker;
        this.init();
    }

    public void init() {

        DataTreeIdentifier<StudentsData> studentDataTreeChangeListener = new DataTreeIdentifier<>(
                LogicalDatastoreType.CONFIGURATION,InstanceIdentifier.builder(StudentsData.class).build());
        listenerRegistration = dataBroker.registerDataTreeChangeListener(
                studentDataTreeChangeListener, this);
        LOG.info("HelloStudentDataListener init start.");
    }

    @Override
    public void onDataTreeChanged(@Nonnull Collection<DataTreeModification<StudentsData>> changes) {
        LOG.info("{} data tree changed.", getClass().getSimpleName());
        for (final DataTreeModification<StudentsData> change : changes) {
            final DataObjectModification<StudentsData> rootChange = change.getRootNode();

            switch (rootChange.getModificationType()) {
                case WRITE:
                    handleWrite(rootChange.getDataBefore(), rootChange.getDataAfter());
                    break;
                case DELETE:
                    handleDelete(rootChange.getDataBefore(), rootChange.getDataAfter());
                    break;
                case SUBTREE_MODIFIED:
                    handleSubtreeModify(rootChange.getDataBefore(), rootChange.getDataAfter());
                    break;
                default:
                    noModify(rootChange.getDataBefore(), rootChange.getDataAfter());
            }
        }
    }

    private void handleWrite(StudentsData before, StudentsData after) {
        //todo  do write change job
        LOG.info("onDataTreeChanged write");
    }

    private void handleDelete(StudentsData before, StudentsData after) {
        //todo  do delete change job
        LOG.info("onDataTreeChanged delete");
    }

    private void handleSubtreeModify(StudentsData before, StudentsData after) {
        //todo  do modify change job
        LOG.info("onDataTreeChanged update");
    }

    private void noModify(StudentsData before, StudentsData after) {
        //todo  do modify change job
        LOG.info("onDataTreeChanged no change ????");
    }


    @Override
    public void close() {
        if (listenerRegistration != null) {
            try {
                listenerRegistration.close();
            } finally {
                listenerRegistration = null;
            }
        }
    }
}
