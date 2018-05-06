# JankMonitor
JankMonitor is a lightweight, highly scalable component for detecting UI Jank on Android.
It is detected based on Choreographer.FrameCallback and Looper.Printer
# Getting started
- BaseFrameCallback

        //support UnitFrameCallback SkipFrameCallback DropFrameCallback AverageFrameCallback
        UnitFrameCallback callback = new UnitFrameCallback(1000);
        callback.setOnUnitFrameFunc(mListener);// setListener
        ChoreographerManager.INSTANCE.start(callback); // get start

        //support custom extends BaseFrameCallback
        BaseFrameCallback callback = new BaseFrameCallback() {
            @Override
            public void onDoFrame(long frameTimeNanos) {
                Log.i(TAG, "onDoFrame: custom base frame callback");
            }
        };

    
- BasePrinter
# Todo
- Complete the Demo and Usage
- Complete the Data-Adapter-Layer with Chain of Responsibility
- Unit Test
- Documents in blog post: the introduce of Smoothness and JankMonitor
- Method Cost Trace Feature
