package com.example.familymapapp.Fragment;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.familymap.R;
import com.example.familymapapp.Extras.DataCache;
import com.example.familymapapp.Extras.FakeServer;

import Model.Person;
import Request.LoginReq;
import Request.RegisterReq;
import Results.EventResult;
import Results.LoginResult;
import Results.PersonIDResult;
import Results.PersonResult;
import Results.RegisterResult;
import Results.Result;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    DataCache dataCache = DataCache.getInstance();

    private EditText mServerHostField;
    private EditText mServerPort;
    private EditText mUserName;
    private EditText mPassword;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmail;
    private RadioGroup mRadioGroup;
    private Button mSignInButton;
    private Button mRegisterButton;

    private String serverHost;
    private String serverPort;
    private String userName;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String gender = "m";



    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LoginFragment.
     */


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private class LoginTask extends AsyncTask<PassInLogin, String, Result> {

        @Override
        protected Result doInBackground(PassInLogin... passInObj) {
            PassInLogin object = passInObj[0];


            LoginReq req = new LoginReq();
            req.setPassword(object.getPassword());
            req.setUserName(object.getUsername());
            publishProgress("Sending Data to the server");
            FakeServer fs = new FakeServer();

            return fs.login(req, object.getHostNumber(), object.getPortNumber());
        }

        @Override
        protected void onPostExecute(Result loginResult) {
            if (loginResult == null) {
                Toast.makeText(getContext(), "Invalid Username/Password, try again!", Toast.LENGTH_LONG).show();
            } else if(!loginResult.isSuccess()){
                Toast.makeText(getContext(), loginResult.getMessage() , Toast.LENGTH_LONG).show();
            }
            else {
                FillFamilyDataTask ffdt = new FillFamilyDataTask();
                LoginResult login = (LoginResult) loginResult;
                PassInData pass = new PassInData(userName, login.getAuthToken(), serverPort, serverHost, login.getPersonID());
                ffdt.execute(pass);
            }
        }

        @Override
        protected void onPreExecute() {

        }

    }


    private static class PassInLogin {
        private String password;
        private String username;
        private String portNumber;
        private String hostNumber;
        PassInLogin(String password, String username, String portNumber, String hostNumber) {
            this.password = password;
            this.username = username;
            this.portNumber = portNumber;
            this.hostNumber = hostNumber;
        }

        public String getPassword() {
            return password;
        }


        public String getUsername() {
            return username;
        }


        public String getPortNumber() {
            return portNumber;
        }


        public String getHostNumber() {
            return hostNumber;
        }

    }


    private class FillFamilyDataTask extends AsyncTask<PassInData, String, Result> {

        @Override
        protected Result doInBackground(PassInData... passInData) {
            PassInData object = passInData[0];
            publishProgress("Sending Data to the server");

            FakeServer fs = new FakeServer();
            PersonResult personResult = (PersonResult) fs.persons(object.getHostNumber(), object.getPortNumber(), object.getAuthToken());
            EventResult eventResult = (EventResult) fs.events(object.getHostNumber(), object.getPortNumber(), object.getAuthToken());

            if(eventResult != null && personResult != null) {
                if (eventResult.isSuccess() && personResult.isSuccess()) {
                    dataCache.setEvents(eventResult.getData());
                    dataCache.setPeople(personResult.getData());
                    return fs.personID(object.getHostNumber(), object.getPortNumber(), object.getAuthToken(), object.getPersonID());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Result loginResult) {
            if(loginResult == null) {
                Toast.makeText(getContext(), "Invalid Username/Password, try again!", Toast.LENGTH_LONG).show();
            }
            else if(!loginResult.isSuccess()) {
                Toast.makeText(getContext(), loginResult.getMessage(), Toast.LENGTH_LONG).show();
            }
            else {
                Person userPerson = new Person();
                userPerson.setPersonID(((PersonIDResult) loginResult).getPersonID());
                userPerson.setFirstName(((PersonIDResult) loginResult).getFirstName());
                userPerson.setLastName(((PersonIDResult) loginResult).getLastName());
                userPerson.setFatherID(((PersonIDResult) loginResult).getFatherID());
                userPerson.setMotherID(((PersonIDResult) loginResult).getMotherID());
                userPerson.setSpouseID(((PersonIDResult) loginResult).getSpouseID());
                userPerson.setGender(((PersonIDResult) loginResult).getGender().toLowerCase());
                userPerson.setUsername(((PersonIDResult) loginResult).getAssociatedUsername());

                dataCache.setMainPerson(userPerson);
                String firstName = ((PersonIDResult) loginResult).getFirstName();
                String lastName = ((PersonIDResult) loginResult).getLastName();
                Toast.makeText(getContext(), "Welcome " + firstName + " " + lastName + "!", Toast.LENGTH_LONG).show();


                MapFragment mapFragment = new MapFragment();
                mapFragment.setFromMain(true);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, mapFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        }

        @Override
        protected void onPreExecute() {

        }

    }

    private class PassInData {
        String username;
        String authToken;
        String portNumber;
        String hostNumber;
        String personID;


        public PassInData(String username, String authToken, String portNumber, String hostNumber, String personID) {
            this.username = username;
            this.authToken = authToken;
            this.portNumber = portNumber;
            this.hostNumber = hostNumber;
            this.personID = personID;
        }

        public String getPortNumber() {
            return portNumber;
        }


        public String getHostNumber() {
            return hostNumber;
        }


        public String getAuthToken() {
            return authToken;
        }


        public String getPersonID() { return personID; }
    }



    private class RegisterTask extends AsyncTask<PassInRegister, String, Result> {

        @Override
        protected Result doInBackground(PassInRegister... passInObj) {
            PassInRegister obj = passInObj[0];
            RegisterReq req = new RegisterReq();
            req.setUserName(obj.getUserName());
            req.setPassword(obj.getPassword());
            req.setEmail(obj.getEmail());
            req.setGender(obj.getGender());
            req.setFirstName(obj.getFirstName());
            req.setLastName(obj.getLastName());

            publishProgress("Sending data to the server");
            FakeServer fs = new FakeServer();

            return fs.register(req, obj.getHostNumber(), obj.getPortNumber());
        }

        @Override
        protected void onPostExecute(Result result) {
            if(result == null) {
                Toast.makeText(getContext(), "Username is taken, suck it!", Toast.LENGTH_LONG).show();
            }
            else if(!result.isSuccess()) {
                Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_LONG).show();
            }
            else {
                FillFamilyDataTask ffdt = new FillFamilyDataTask();
                RegisterResult login = (RegisterResult) result;
                PassInData pass = new PassInData(userName, login.getAuthToken(), serverPort, serverHost, login.getPersonID());
                ffdt.execute(pass);
            }
        }

        @Override
        protected void onPreExecute() {}

    }

    private class PassInRegister {
        private String userName;
        private String password;
        private String email;
        private String firstName;
        private String  lastName;
        private String gender;
        private String portNumber;
        private String hostNumber;

        public PassInRegister(String userName, String password, String email, String firstName, String lastName, String gender, String portNumber, String hostNumber) {
            this.userName = userName;
            this.password = password;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.gender = gender;
            this.portNumber = portNumber;
            this.hostNumber = hostNumber;
        }

        public String getUserName() {
            return userName;
        }


        public String getPassword() {
            return password;
        }


        public String getEmail() {
            return email;
        }

        public String getFirstName() {
            return firstName;
        }


        public String getLastName() {
            return lastName;
        }


        public String getGender() {
            return gender;
        }


        public String getPortNumber() {
            return portNumber;
        }


        public String getHostNumber() {
            return hostNumber;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mServerHostField = view.findViewById(R.id.server_host_id);
        mServerPort = view.findViewById(R.id.server_port_id);
        mUserName = view.findViewById(R.id.username_id);
        mPassword = view.findViewById(R.id.password_id);
        mFirstName = view.findViewById(R.id.first_name_id);
        mLastName = view.findViewById(R.id.last_name_id);
        mEmail = view.findViewById(R.id.email_id);
        mSignInButton = view.findViewById(R.id.sign_in_button);
        mRegisterButton = view.findViewById(R.id.register_button);
        mRadioGroup = view.findViewById(R.id.gender_id);

        mServerHostField.addTextChangedListener(loginTextWatcher);
        mServerPort.addTextChangedListener(loginTextWatcher);
        mUserName.addTextChangedListener(loginTextWatcher);
        mPassword.addTextChangedListener(loginTextWatcher);
        mFirstName.addTextChangedListener(loginTextWatcher);
        mLastName.addTextChangedListener(loginTextWatcher);
        mEmail.addTextChangedListener(loginTextWatcher);
        mSignInButton.addTextChangedListener(loginTextWatcher);
        mRegisterButton.addTextChangedListener(loginTextWatcher);
        mRegisterButton.setEnabled(false);
        mSignInButton.setEnabled(false);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedID) {
                int genderID = mRadioGroup.getCheckedRadioButtonId();
                switch (genderID) {
                    case R.id.male_radio:
                        //System.out.println("m");
                        gender = "m";
                        break;
                    case R.id.female_radio:
                        //System.out.println("f");
                        gender = "f";
                        break;
                }
            }
        });

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final PassInLogin login = new PassInLogin(password, userName, serverPort, serverHost);
                LoginTask aSync = new LoginTask();
                aSync.execute(login);
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final PassInRegister register = new PassInRegister(userName, password, email, firstName, lastName, gender, serverPort, serverHost);
                RegisterTask rTask = new RegisterTask();
                rTask.execute(register);
            }
        });
        return view;
    }

    static boolean isValid(String email) {
        String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
        return email.matches(regex);
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            serverHost = mServerHostField.getText().toString().trim();
            serverPort = mServerPort.getText().toString().trim();
            userName = mUserName.getText().toString().trim();
            password = mPassword.getText().toString().trim();
            firstName = mFirstName.getText().toString().trim();
            lastName = mLastName.getText().toString().trim();
            email = mEmail.getText().toString().trim();

            mSignInButton.setEnabled(!serverHost.isEmpty() && !serverPort.isEmpty() && !userName.isEmpty() && !password.isEmpty());
            mRegisterButton.setEnabled(!serverHost.isEmpty() && !serverPort.isEmpty() && !userName.isEmpty() && !password.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty() && isValid(email));
        }

        @Override
        public void afterTextChanged(Editable s) {

        }

    };
}
