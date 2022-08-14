/*
 * Copyright © 2017 dxj and others.  All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.odl.hello.impl;

import com.google.common.util.concurrent.Futures;
import java.util.concurrent.Future;
import org.opendaylight.controller.md.sal.binding.api.DataBroker;
import org.opendaylight.controller.md.sal.binding.api.NotificationPublishService;
import org.opendaylight.controller.md.sal.binding.api.WriteTransaction;

import org.opendaylight.controller.md.sal.common.api.data.LogicalDatastoreType;

import org.opendaylight.controller.md.sal.common.api.data.TransactionCommitFailedException;

//import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev170830.*;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev170830.AddStudentInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev170830.GetStudentInfoInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev170830.GetStudentInfoOutput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev170830.HelloService;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev170830.PublishStudentNotificationInput;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev170830.StudentPlayTruant;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev170830.StudentPlayTruantBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev170830.StudentsData;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev170830.students.data.Students;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev170830.students.data.StudentsBuilder;
import org.opendaylight.yang.gen.v1.urn.opendaylight.params.xml.ns.yang.hello.rev170830.students.data.StudentsKey;
import org.opendaylight.yangtools.yang.binding.InstanceIdentifier;
import org.opendaylight.yangtools.yang.common.RpcError;
import org.opendaylight.yangtools.yang.common.RpcResult;
import org.opendaylight.yangtools.yang.common.RpcResultBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class HelloProvider implements HelloService {

    private static final Logger LOG = LoggerFactory.getLogger(HelloProvider.class);
//    private static DataBroker dataBroker;
    private HelloStudentDataListener helloStudentDataListener;

    private final DataBroker dataBroker;
    private final NotificationPublishService  notificationPublishService;

    public HelloProvider(DataBroker dataBroker,NotificationPublishService notificationPublishService) {
//        HelloProvider.dataBroker = dataBroker ;
        this.dataBroker = dataBroker;
        this.notificationPublishService = notificationPublishService;

    }




    /**
     * Method called when the blueprint container is created.
     */
    public void init() {
        helloStudentDataListener  = new   HelloStudentDataListener(dataBroker);

        LOG.info("HelloProvider Session Initiated");
    }

    /**
     * Method called when the blueprint container is destroyed.
     */
    public void close() {
        helloStudentDataListener.close();
        LOG.info("HelloProvider Closed");
    }

    @Override
    public Future<RpcResult<GetStudentInfoOutput>> getStudentInfo(GetStudentInfoInput input) {
        return null;
    }

    @Override
    public Future<RpcResult<Void>> publishStudentNotification(PublishStudentNotificationInput input) {
        StudentPlayTruant studentPlayTruant = new StudentPlayTruantBuilder()
                .setLesson(input.getLesson())
                .setName(input.getName())
                .setNumber(input.getNumber())
                .setReason(input.getReason())
                .build();

        notificationPublishService.offerNotification(studentPlayTruant);
        return  Futures.immediateFuture(RpcResultBuilder.<Void>success().build());


    }

    @Override
    public Future<RpcResult<Void>> addStudent(AddStudentInput input) {
        WriteTransaction wt = this.dataBroker.newWriteOnlyTransaction();
        InstanceIdentifier<Students> studentsIid =  InstanceIdentifier.builder(StudentsData.class).child(Students.class,
                new StudentsKey(input.getNumber())).build();
        Students students = new StudentsBuilder(input).build();
        wt.put(LogicalDatastoreType.CONFIGURATION,studentsIid,students);
        try {
            wt.submit().checkedGet();
        } catch (TransactionCommitFailedException e) {
            LOG.error("addStudent error with exc:", e);
            return Futures.immediateFuture(RpcResultBuilder.<Void>failed().withError(RpcError.ErrorType.RPC,
                    "addStudent fail !").build());
        }

        return Futures.immediateFuture(RpcResultBuilder.<Void>success().build());
    }
}