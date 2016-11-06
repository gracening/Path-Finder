package ca.uwaterloo.lab4_203_31;

import java.util.LinkedList;

import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ca.uwaterloo.lab4_203_31.mapper.FloatHelper;
import ca.uwaterloo.lab4_203_31.mapper.IMapperListener;
import ca.uwaterloo.lab4_203_31.mapper.MapLoader;
import ca.uwaterloo.lab4_203_31.mapper.Mapper;
import ca.uwaterloo.lab4_203_31.mapper.PedometerMap;

public class MainActivity extends ActionBarActivity {
	static float NS = 0;
	static float EW = 0;
	static int step = 0;
	static boolean isStart = false;
	static Button start = null;
	static Mapper mapView;
	static PathFinder finder;
	static final float distanceTolerance = 0.5f;
	static final int xLength = 125;
	static final int yLength = 125;
	static final int unit = 5;
	static final int criticalPointsDistance = 7;
	static final float stepDistance = 0.7f;
	static LinkedList<PointF> path;
	static LinkedList<PointF> criticalPoints;
	static PointF[] currentPath = new PointF[2];
	static float currentAngle = 0;
	static View rootView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mapView = new Mapper(getApplicationContext(), 1000, 1000, 40, 40);
		registerForContextMenu(mapView);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			rootView = inflater.inflate(R.layout.fragment_main, container, false);
			LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.layout);

			TextView display1 = new TextView(rootView.getContext());
			display1.setText("");
			layout.addView(display1);

			TextView display2 = new TextView(rootView.getContext());
			display2.setText("Step: " + step);
			layout.addView(display2);

			SensorManager sensorManager_main = (SensorManager) rootView.getContext().getSystemService(SENSOR_SERVICE);

			Sensor rotationVector = sensorManager_main.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
			Sensor linearAcceleration = sensorManager_main.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
			Sensor gravity = sensorManager_main.getDefaultSensor(Sensor.TYPE_GRAVITY);

			SensorEventListener listeners = new MySensorListener(display1, display2);

			sensorManager_main.registerListener(listeners, rotationVector, SensorManager.SENSOR_DELAY_FASTEST);
			sensorManager_main.registerListener(listeners, linearAcceleration, SensorManager.SENSOR_DELAY_FASTEST);
			sensorManager_main.registerListener(listeners, gravity, SensorManager.SENSOR_DELAY_FASTEST);

			Button reset = new Button(rootView.getContext());
			reset.setText("Reset");
			reset.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					EW = 0;
					NS = 0;
					step = 0;
				}
			});
			start = new Button(rootView.getContext());
			start.setText("Start");
			start.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					isStart = !isStart;
					if (isStart) {
						mapView.setUserPoint(path.get(0));
						currentPath[0] = mapView.getUserPoint();
						currentPath[1] = criticalPoints.get(0);
						currentAngle = FloatHelper.getRotateAngle(currentPath[1], currentPath[0],
								new PointF(currentPath[1].x, currentPath[1].y + 1));
						finder.removeLinearObstacle(2.2f, 17.2f, 17.6f, 18f);
						start.setText("Pause");
					} else {
						start.setText("Start");
					}
				}
			});

			layout.addView(reset);
			layout.addView(start);

			MapLoader mapLoader = new MapLoader();
			PedometerMap map = mapLoader.loadMap(rootView.getContext().getExternalFilesDir(null), "E2-3344.svg");
			mapView.setMap(map);
			layout.addView(mapView);

			myIMapperListener listener = new myIMapperListener();
			mapView.addListener(listener);

			PathPoint[][] pathMap = new PathPoint[xLength][yLength];
			for (int j = 0; j < yLength; j++) {
				for (int i = 0; i < xLength; i++) {
					pathMap[i][j] = new PathPoint(i, j, true);
				}
			}

			finder = new PathFinder(mapView, pathMap, xLength, yLength, unit);
			finder.setLinearObstacle(0f, 2f, 0f, 24.8f);
			finder.setLinearObstacle(0f, 24.8f, 0f, 2.2f);
			finder.setLinearObstacle(2f, 3.8f, 2.2f, 18f);
			finder.setLinearObstacle(7f, 10.6f, 2.2f, 18f);
			finder.setLinearObstacle(13.8f, 17.2f, 2.2f, 18f);
			finder.setLinearObstacle(20.6f, 24.8f, 2.2f, 4.2f);
			finder.setLinearObstacle(4.4f, 19.8f, 20f, 24.8f);
			finder.setLinearObstacle(22.6f, 24.8f, 6.8f, 19.4f);
			finder.setLinearObstacle(21.4f, 24.8f, 21f, 24.8f);
			finder.setLinearObstacle(24.4f, 24.8f, 0f, 24.8f);
			finder.setLinearObstacle(0f, 24.8f, 21.2f, 24.8f);
			
			return rootView;
		}
	}

	public static void setCriticalPoints() {
		criticalPoints = new LinkedList<PointF>();
		PointF current = path.get(0);
		PointF previous = null;
		int i = 1;
		while (i < path.size()) {
			if (path.get(i).x != current.x && path.get(i).y != current.y && previous != null) {
				current = previous;
				criticalPoints.add(current);
			} else if (FloatHelper.distance(path.get(i), current) >= criticalPointsDistance) {
				current = path.get(i);
				criticalPoints.add(current);
			}
			previous = path.get(i);
			i++;
		}
	}

	static class myIMapperListener implements IMapperListener {

		public myIMapperListener() {
		}

		@Override
		public void locationChanged(Mapper source, PointF loc) {
			finder.setStart(loc.x, loc.y);
			source.setUserPoint(loc);
		}

		@Override
		public void DestinationChanged(Mapper source, PointF dest) {
			finder.setEnd(dest.x, dest.y);
			path = finder.getPath();
			setCriticalPoints();
			int j = 0;
			mapView.removeAllLabeledPoints();
			mapView.setUserPath(path);
			for (PointF p : criticalPoints) {
				mapView.addLabeledPoint(p, "" + j);
				j++;
			}
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		mapView.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return super.onContextItemSelected(item) || mapView.onContextItemSelected(item);
	}

	public static class MySensorListener implements SensorEventListener {
		TextView output1;
		TextView output2;

		// sensor values
		private float[] rotationVector = new float[3];

		//
		private float[] rotationMatrix = new float[16];
		private float[] orientationValues = { 0, 0, 0 };
		private float[] lastOrientationValues = { 0, 0, 0 };

		// pedometer variables
		private float[] angle = { 0, 0, 0 };
		private long currentMill = System.currentTimeMillis();
		private long previousSetp = System.currentTimeMillis();
		private boolean isIncreasing = true;
		private boolean halfStep = false;
		private final float high1 = (float) 1.5;
		private final float high2 = (float) 2.3;
		private final float low1 = (float) 0.4;
		private final float low2 = (float) 0.7;
		private float[] linear_acceleration = { 0, 0, 0 };
		private float previousResult = 0;
		private float absPreviousResult = 0;
		private float absResult = 0;
		private float result = 0;

		public MySensorListener(TextView view1, TextView view2) {
			output1 = view1;
			output2 = view2;
		}

		private float averageOrientation(float a, float b) {
			if (Math.abs(a - b) > (float) Math.PI) {
				return (float) ((a + b) / 2 + Math.PI);
			} else {
				return (a + b) / 2;
			}
		}

		public void onAccuracyChanged(Sensor s, int i) {
		}

		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
				System.arraycopy(event.values, 0, rotationVector, 0, 3);
				SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector);
				SensorManager.getOrientation(rotationMatrix, orientationValues);
				output1.setText("Degree: " + String.format("% .0f", (360 + orientationValues[0] / Math.PI * 180) % 360)
						+ " Walk to :" + String.format("% .0f", (180 + currentAngle) % 360));
			} else if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
				angle[0] = (float) (event.values[0] / 9.81);
				angle[1] = (float) (event.values[1] / 9.81);
				angle[2] = (float) (event.values[2] / 9.81);
			} else if (isStart && event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
				linear_acceleration[2] += (event.values[2] - linear_acceleration[2]) / 5;
				result += (angle[2] * linear_acceleration[2] - previousResult) / 5;
				absResult = Math.abs(result);
				if (isIncreasing == true && absResult < absPreviousResult) {
					isIncreasing = false;
					currentMill = System.currentTimeMillis();
					if (halfStep == true && currentMill - previousSetp > 333 && absResult > low1 && absResult < high1) {
						float result1 = (float) (stepDistance
								* Math.sin(averageOrientation(orientationValues[0], lastOrientationValues[0])));
						float result2 = (float) (stepDistance
								* Math.cos(averageOrientation(orientationValues[0], lastOrientationValues[0])));
						EW += result1;
						NS += result2;
						PointF user = mapView.getUserPoint();
						PointF newUser = new PointF(user.x + result1, user.y + result2);
						if (!finder.isTraversable(newUser)) {
							newUser = user;
						}
						mapView.setUserPoint(newUser);
						if (FloatHelper.distance(newUser, currentPath[1]) < 0.5) {
							if (currentPath[1].equals(mapView.getEndPoint())) {
								Toast toast = Toast.makeText(rootView.getContext(), "Reach!", Toast.LENGTH_SHORT);
								toast.show();
							} else {
								criticalPoints.remove(0);
								mapView.removeAllLabeledPoints();
								if (!criticalPoints.isEmpty()) {
									int j = 0;
									for (PointF p : criticalPoints) {
										mapView.addLabeledPoint(p, "" + j);
										j++;
									}
									currentPath[1] = criticalPoints.get(0);
								} else {
									currentPath[1] = mapView.getEndPoint();
								}
							}
						}
						currentPath[0] = newUser;
						currentAngle = FloatHelper.getRotateAngle(currentPath[1], currentPath[0],
								new PointF(currentPath[1].x, currentPath[1].y + 1));
						step++;
						halfStep = false;
						previousSetp = currentMill;
					} else if (halfStep == false && absResult > low2 && absResult < high2) {
						halfStep = true;
						System.arraycopy(orientationValues, 0, lastOrientationValues, 0, 3);
					}
				} else if (isIncreasing == false && absResult > absPreviousResult) {
					isIncreasing = true;
				}
				SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector);
				SensorManager.getOrientation(rotationMatrix, orientationValues);
				output2.setText("Step: " + step);
				previousResult = result;
				absPreviousResult = Math.abs(previousResult);
			}
		}
	}
}
