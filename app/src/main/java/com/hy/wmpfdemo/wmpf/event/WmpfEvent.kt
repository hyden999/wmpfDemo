package com.hy.wmpfdemo.wmpf.event

import com.vise.xsnow.event.IEvent

/**
 * @author hy
 * @date 2020-07-14
 */
data class WmpfEvent(var invokeId: String?, var command: String?, var sourceData: String?) : IEvent