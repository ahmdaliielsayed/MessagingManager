package com.message.messagingmanager

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import android.view.accessibility.AccessibilityEvent

class WhatsappAccessibilityService : AccessibilityService() {

    val TAG = "AutoMsgService"

    private fun getEventType(event: AccessibilityEvent): String {

        when (event.eventType) {
            AccessibilityEvent.TYPE_VIEW_CLICKED -> return "TYPE_VIEW_CLICKED"
            AccessibilityEvent.TYPE_VIEW_LONG_CLICKED -> return "TYPE_VIEW_LONG_CLICKED"
            AccessibilityEvent.TYPE_VIEW_FOCUSED -> return "TYPE_VIEW_FOCUSED"
            AccessibilityEvent.TYPE_VIEW_SELECTED -> return "TYPE_VIEW_SELECTED"
            AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> return "TYPE_VIEW_TEXT_CHANGED"
            AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> return "TYPE_WINDOW_STATE_CHANGED"
            AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> return "TYPE_NOTIFICATION_STATE_CHANGED"
            AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START -> return "TYPE_TOUCH_EXPLORATION_GESTURE_START"
            AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END -> return "TYPE_TOUCH_EXPLORATION_GESTURE_END"
            AccessibilityEvent.TYPE_VIEW_HOVER_ENTER -> return "TYPE_VIEW_HOVER_ENTER"
            AccessibilityEvent.TYPE_VIEW_HOVER_EXIT -> return "TYPE_VIEW_HOVER_EXIT"
            AccessibilityEvent.TYPE_VIEW_SCROLLED -> return "TYPE_VIEW_SCROLLED"
            AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> return "TYPE_VIEW_TEXT_SELECTION_CHANGED"
            AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> return "TYPE_WINDOW_CONTENT_CHANGED"
            AccessibilityEvent.TYPE_ANNOUNCEMENT -> return "TYPE_ANNOUNCEMENT"
            AccessibilityEvent.TYPE_GESTURE_DETECTION_START -> return "TYPE_GESTURE_DETECTION_START"
            AccessibilityEvent.TYPE_GESTURE_DETECTION_END -> return "TYPE_GESTURE_DETECTION_END"
            AccessibilityEvent.TYPE_TOUCH_INTERACTION_START -> return "TYPE_TOUCH_INTERACTION_START"
            AccessibilityEvent.TYPE_TOUCH_INTERACTION_END -> return "TYPE_TOUCH_INTERACTION_END"
            AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED -> return "TYPE_VIEW_ACCESSIBILITY_FOCUSED"
            AccessibilityEvent.TYPE_WINDOWS_CHANGED -> return "TYPE_WINDOWS_CHANGED"
            AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED -> return "TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED"
        }
        println("getEventType")
        return "default"
    }

    private fun printChildNodes(nodeInfo: AccessibilityNodeInfo, spaces: String): Boolean {
//        Log.i(
//            TAG,
//            spaces + "Classname " + nodeInfo.className.toString() + ", " + nodeInfo.viewIdResourceName
//        )
//        val childCount = nodeInfo.childCount
//        if (childCount == 0)
//            return false
//        var nodeText = ""
//        if (nodeInfo.text != null)
//            nodeText = nodeInfo.text.toString()
//        Log.i(TAG, "$spaces$nodeText child count $childCount")
//        for (i in 0 until childCount) {
//            val childNode = nodeInfo.getChild(i) ?: continue
//            if (childNode.text != null) {
//                Log.i(TAG, spaces + "child:" + i.toString() + "-" + childNode.text.toString())
//            }
//            if (printChildNodes(childNode, "$spaces  "))
//                return true
//        }
        return false
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (!sActive || event.source == null) {
            Log.i(TAG, "Quitting " + sActive + event.source)
            return
        }

        var nodeInfo: AccessibilityNodeInfo? = event.source.parent
        if (nodeInfo == null) {
            Log.i(TAG, "Parent is null")
            nodeInfo = event.source
        }

        val contactNameNodes =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                nodeInfo!!.findAccessibilityNodeInfosByViewId("com.whatsapp:id/conversation_contact_name")
            } else {
                TODO("VERSION.SDK_INT < JELLY_BEAN_MR2")
            }

        if (contactNameNodes.size == 0) {
            Log.i(TAG, "contact name not visible")
            // contactNameNodes.get(0)
            return
        } else if (contactNameNodes.size == 1) {

            if (contactNameNodes[0].text.toString() == sContact) {
                Log.i(TAG, "Found $sContact")

                if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || event.eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED || true) {
                    printChildNodes(nodeInfo, "  ")
                    val entryETNodes = nodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/entry")
                    Log.i(TAG, "editing text " + entryETNodes.size)

                    if (entryETNodes.size == 1) {
                        val arguments = Bundle()

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, sMsg)
                            entryETNodes[0].performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                            Log.i(TAG, "entryETNodes.size  ${entryETNodes.size} " + entryETNodes.size)
                        }
                    }
                } else if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED || event.eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED || true) {

                    if (nodeInfo.viewIdResourceName == null) {
                        val sendBtnNodes = nodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send")
                        Log.i(TAG, "clicking button" + sendBtnNodes.size)

                        if (sendBtnNodes.size == 1) {
                            val entryETNodes = nodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/entry")
                            Log.i(TAG, "entry nodes size " + entryETNodes.size)

                            if (entryETNodes.size == 1) {
                                Log.i(TAG, "entry node text " + entryETNodes[0].text)

                                if (entryETNodes[0].text.toString().equals(sMsg)) {
                                    sendBtnNodes[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                                    sActive = false
                                }
                            }
                        }
                    }
                } else {
                    println("nothing!!!!!")
                    printChildNodes(nodeInfo, "  ")
                }
            }else {
                Log.i(TAG, "Wrong contact " + contactNameNodes[0].text)
                return
            }
        }

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || event.eventType == AccessibilityEvent.TYPE_VIEW_FOCUSED) {
            printChildNodes(nodeInfo, "  ")
            val entryETNodes = nodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/entry")
            Log.i(TAG, "editing text " + entryETNodes.size)

            if (entryETNodes.size == 1) {
                val arguments = Bundle()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, sMsg)
                    entryETNodes[0].performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                    Log.i(TAG, "entryETNodes.size  ${entryETNodes.size} " + entryETNodes.size)
                }
            }
        } else if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED || true) {

            if (nodeInfo.viewIdResourceName == null) {

                val sendBtnNodes = nodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send")
                Log.i(TAG, "clicking button" + sendBtnNodes.size)

                if (sendBtnNodes.size == 1) {
                    val entryETNodes = nodeInfo.findAccessibilityNodeInfosByViewId("com.whatsapp:id/entry")
                    Log.i(TAG, "entry nodes size " + entryETNodes.size)

                    if (entryETNodes.size == 1) {

                        Log.i(TAG, "entry node text " + entryETNodes[0].text)

                        if (entryETNodes[0].text.toString() == sMsg) {
                            sendBtnNodes[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
                            sActive = false
                        }
                    }
                }
            }
        } else
            printChildNodes(nodeInfo, " ")
        nodeInfo.recycle()
    }

    override fun onInterrupt() {

    }

    override fun onServiceConnected() {

        super.onServiceConnected()
        myInstance = this
        Log.i(TAG, "service connected")
    }


    companion object {
        var sActive = false
        var sContact = "Empty"
        var sMsg = "Empty"
        var sPhone: String ="Empty"
        @SuppressLint("StaticFieldLeak")
        var myInstance: WhatsappAccessibilityService? = null
        @SuppressLint("StaticFieldLeak")
        var sContext: Context?=null

        fun getMInstance(): WhatsappAccessibilityService? {
            return myInstance
        }
    }
}