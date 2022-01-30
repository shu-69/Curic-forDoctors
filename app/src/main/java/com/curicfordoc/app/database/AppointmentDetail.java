package com.curicfordoc.app.database;

public class AppointmentDetail {
    private String orderId;
    private String docId;
    private String paymentId;
    private String appointmentTime;
    private String appointmentDate;
    private String patientName;
    private String patientAge;
    private String patientGender;
    private String patientContact;
    private String patientEmail;
    private String patientAddress;
    private String doctorFee;
    private String patientLoginMethod;
    private String patientLoginId;
    private int status; // 0 : successful , 1 : cancelled
    private boolean isDone;
    private boolean isPinned;

    public AppointmentDetail(String orderId, String docId, String paymentId, String appointmentTime, String appointmentDate, String patientName, String patientAge, String patientGender, String patientContact, String patientEmail, String patientAddress, String doctorFee, String patientLoginMethod, String patientLoginId, int status, boolean isDone, boolean isPinned) {
        this.orderId = orderId;
        this.docId = docId;
        this.paymentId = paymentId;
        this.appointmentTime = appointmentTime;
        this.appointmentDate = appointmentDate;
        this.patientName = patientName;
        this.patientAge = patientAge;
        this.patientGender = patientGender;
        this.patientContact = patientContact;
        this.patientEmail = patientEmail;
        this.patientAddress = patientAddress;
        this.doctorFee = doctorFee;
        this.patientLoginMethod = patientLoginMethod;
        this.patientLoginId = patientLoginId;
        this.status = status;
        this.isDone = isDone;
        this.isPinned = isPinned;
    }

    public AppointmentDetail() {
    }

    public String getOrderId() {
        return orderId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientAge() {
        return patientAge;
    }

    public void setPatientAge(String patientAge) {
        this.patientAge = patientAge;
    }

    public String getPatientGender() {
        return patientGender;
    }

    public void setPatientGender(String patientGender) {
        this.patientGender = patientGender;
    }

    public String getPatientContact() {
        return patientContact;
    }

    public void setPatientContact(String patientContact) {
        this.patientContact = patientContact;
    }

    public String getPatientEmail() {
        return patientEmail;
    }

    public void setPatientEmail(String patientEmail) {
        this.patientEmail = patientEmail;
    }

    public String getPatientAddress() {
        return patientAddress;
    }

    public void setPatientAddress(String patientAddress) {
        this.patientAddress = patientAddress;
    }

    public String getDoctorFee() {
        return doctorFee;
    }

    public void setDoctorFee(String doctorFee) {
        this.doctorFee = doctorFee;
    }

    public String getPatientLoginMethod() {
        return patientLoginMethod;
    }

    public void setPatientLoginMethod(String patientLoginMethod) {
        this.patientLoginMethod = patientLoginMethod;
    }

    public String getPatientLoginId() {
        return patientLoginId;
    }

    public void setPatientLoginId(String patientLoginId) {
        this.patientLoginId = patientLoginId;
    }

    public boolean getDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean getPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }
}











