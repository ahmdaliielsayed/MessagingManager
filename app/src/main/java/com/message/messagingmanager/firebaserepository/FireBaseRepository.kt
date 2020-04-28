package com.message.messagingmanager.firebaserepository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.message.messagingmanager.R
import com.message.messagingmanager.model.Group
import com.message.messagingmanager.model.Message
import com.message.messagingmanager.model.SIM
import com.message.messagingmanager.viewmodel.*

class FireBaseRepository {

    private lateinit var signInViewModel: SignInViewModel
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var forgetPasswordViewModel: ForgetPasswordViewModel
    private lateinit var scheduleMessageViewModel: ScheduleMessageViewModel
    private lateinit var editScheduleMessageViewModel: EditScheduleMessageViewModel
    private var mAuth: FirebaseAuth
    private lateinit var databaseReferenceMsg: DatabaseReference
    private lateinit var createGroupViewModel: CreateGroupViewModel
    private lateinit var databaseReferenceGroup: DatabaseReference
    private lateinit var networksViewModel: NetworksViewModel
    private lateinit var databaseReferenceSIM: DatabaseReference

    constructor(signInViewModel: SignInViewModel) {
        this.signInViewModel = signInViewModel
        mAuth = FirebaseAuth.getInstance()
    }
    fun loginRepository(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    if (mAuth.currentUser!!.isEmailVerified) {
                        signInViewModel.openHomeActivity()
                    } else {
                        signInViewModel.setMsgAlert(R.string.verifyEmail)
                    }
                } else {
                    task.exception?.message?.let {
                        signInViewModel.setMsgAlert(it)
                    }
                }
            }
    }

    constructor(signUpViewModel: SignUpViewModel) {
        this.signUpViewModel = signUpViewModel
        mAuth = FirebaseAuth.getInstance()
    }
    @Suppress("NAME_SHADOWING")
    fun signUpRepository(email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                when {
                    task.isSuccessful -> mAuth.currentUser!!.sendEmailVerification()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                signUpViewModel.setMsgAlert(R.string.registerSuccessfully)
                            } else {
                                task.exception?.message?.let {
                                    signUpViewModel.setMsgAlert(it)
                                }
                            }
                        }
                    task.exception is FirebaseAuthUserCollisionException -> signUpViewModel.setMsgAlert(R.string.authenticationFailed, 1)
                    else -> signUpViewModel.setMsgAlert(R.string.commentError)
                }
            }
    }

    constructor(forgetPasswordViewModel: ForgetPasswordViewModel) {
        this.forgetPasswordViewModel = forgetPasswordViewModel
        mAuth = FirebaseAuth.getInstance()
    }
    fun changePasswordRepository(email: String) {
        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    forgetPasswordViewModel.setMsgAlert(R.string.emailSentSuccessfully)
                } else {
                    task.exception?.message?.let {
                        forgetPasswordViewModel.setMsgAlert(it)
                    }
                }
            }
    }

    constructor(scheduleMessageViewModel: ScheduleMessageViewModel) {
        this.scheduleMessageViewModel = scheduleMessageViewModel
        mAuth = FirebaseAuth.getInstance()
        databaseReferenceMsg = FirebaseDatabase.getInstance().getReference("Messages")
    }
    fun scheduleMessageRepository(personName: String, receiverNumber: String, SMSMessage: String, date: String, time: String, status: String, type: String, calendar: Long) {

        // unique id for each msg
        val smsId: String = databaseReferenceMsg.push().key.toString()
        val message = Message(smsId, personName, receiverNumber, SMSMessage, date, time, status, type,
            FirebaseAuth.getInstance().currentUser!!.uid, calendar)
        // store this msg to fireBase
        databaseReferenceMsg.child(smsId).setValue(message)

        scheduleMessageViewModel.setSMSAlarm(message.getSmsId(), message.getSmsReceiverName(),
            message.getSmsReceiverNumber(), message.getSmsMsg(), message.getSmsDate(),
            message.getSmsTime(), message.getSmsStatus(), message.getSmsType(), message.getUserID(),
            message.getSmsCalender())
    }
    fun scheduleWhatsAppMessageRepository(personName: String, receiverNumber: String, SMSMessage: String, date: String, time: String, status: String, type: String, calendar: Long) {

        // unique id for each msg
        val smsId: String = databaseReferenceMsg.push().key.toString()
        val message = Message(smsId, personName, receiverNumber, SMSMessage, date, time, status, type,
            FirebaseAuth.getInstance().currentUser!!.uid, calendar)
        // store this msg to fireBase
        databaseReferenceMsg.child(smsId).setValue(message)

        scheduleMessageViewModel.setWhatsAppMessageAlarm(message.getSmsId(), message.getSmsReceiverName(),
            message.getSmsReceiverNumber(), message.getSmsMsg(), message.getSmsDate(),
            message.getSmsTime(), message.getSmsStatus(), message.getSmsType(), message.getUserID(),
            message.getSmsCalender())
    }

    constructor(createGroupViewModel: CreateGroupViewModel) {
        this.createGroupViewModel = createGroupViewModel
        mAuth = FirebaseAuth.getInstance()
        databaseReferenceGroup = FirebaseDatabase.getInstance().getReference("Groups")
    }
    fun createGroupRepository(name: String) {

        // unique id for each msg
        val groupId: String = databaseReferenceGroup.push().key.toString()
        val group = Group(groupId, name, FirebaseAuth.getInstance().currentUser!!.uid)
        // store this msg to fireBase
        databaseReferenceGroup.child(groupId).setValue(group)

        createGroupViewModel.goToSelectContacts(groupId)
    }

    constructor(editScheduleMessageViewModel: EditScheduleMessageViewModel) {
        this.editScheduleMessageViewModel = editScheduleMessageViewModel
        mAuth = FirebaseAuth.getInstance()
        databaseReferenceMsg = FirebaseDatabase.getInstance().getReference("Messages")
    }
    fun editScheduleMessageRepository(smsId: String, personName: String, receiverNumber: String, SMSMessage: String, date: String, time: String, status: String, type: String, calendar: Long) {

        val message = Message(smsId, personName, receiverNumber, SMSMessage, date, time, status, type,
            FirebaseAuth.getInstance().currentUser!!.uid, calendar)
        // store this msg to fireBase
        databaseReferenceMsg.child(smsId).setValue(message)

        editScheduleMessageViewModel.setSMSAlarm(message.getSmsId(), message.getSmsReceiverName(),
            message.getSmsReceiverNumber(), message.getSmsMsg(), message.getSmsDate(),
            message.getSmsTime(), message.getSmsStatus(), message.getSmsType(), message.getUserID(),
            message.getSmsCalender())
    }
    fun editScheduleWhatsAppMessageRepository(smsId: String, personName: String, receiverNumber: String, SMSMessage: String, date: String, time: String, status: String, type: String, calendar: Long) {

        val message = Message(smsId, personName, receiverNumber, SMSMessage, date, time, status, type,
            FirebaseAuth.getInstance().currentUser!!.uid, calendar)
        // store this msg to fireBase
        databaseReferenceMsg.child(smsId).setValue(message)

        editScheduleMessageViewModel.setWhatsAppMessageAlarm(message.getSmsId(), message.getSmsReceiverName(),
            message.getSmsReceiverNumber(), message.getSmsMsg(), message.getSmsDate(),
            message.getSmsTime(), message.getSmsStatus(), message.getSmsType(), message.getUserID(),
            message.getSmsCalender())
    }

    constructor(networksViewModel: NetworksViewModel) {
        this.networksViewModel = networksViewModel
        mAuth = FirebaseAuth.getInstance()
        databaseReferenceSIM = FirebaseDatabase.getInstance().getReference("SIMs")
    }
    fun addSIMRepository(SIMName: String, SIMPrefix: String) {

        val simId: String = databaseReferenceSIM.push().key.toString()
        val sim = SIM(simId, SIMName, SIMPrefix, FirebaseAuth.getInstance().currentUser!!.uid)
        databaseReferenceSIM.child(simId).setValue(sim)

        networksViewModel.setMsgToast(R.string.simAdded)
    }
}