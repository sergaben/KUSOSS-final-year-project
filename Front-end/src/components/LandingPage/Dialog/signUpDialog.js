import React, {Component} from 'react';
import Dialog from 'material-ui/Dialog';
import { Field,reduxForm } from 'redux-form';
import {FlatButton} from 'material-ui';
import {TextField, SelectField} from 'redux-form-material-ui';
import MenuItem from 'material-ui/MenuItem';
import axios from 'axios';
import Axios from '../../../util/axiosFunction';
class LoginDialog extends Component{
        constructor(){
            super();
            this.state = {
                nickname:'',
                email:'',
                password:'',
                subject:[],
                typeOfStudy:''
            }
        }
        generateURL= (prodMode, url)=>{
            if(!prodMode){
                return `localhost:9000/${url}`;
            }
            return `https://kussos-backend.herokuapp.com/${url}`;
        }
        componentDidMount(){
            axios({
                method:'get',
                url:'https://kussos-backend.herokuapp.com/getSubjectsNamesAsJson'
            }).then((response)=>{
                //console.log(response.data);
                this.setState(()=>{
                    return{
                        subject:response.data
                    }
                })
            }).catch((error)=>{
                console.log(error);
            })
        }
        onChange = (e) =>{
            const state = this.state;
            state[e.target.name] = e.target.value;
            this.setState(state);
        }
        generateMenuItems = () =>{
            let menuItems = [] // empty array which will hold all the values from the database
            this.state.subject.map((array,index)=>{
                menuItems.push(<MenuItem key={index} value={array.name} primaryText={array.name}/>); // this will put all the menuItems in the array
            })
            return menuItems;
        }
        onDropdownSelected = (e)=>{
            console.log("The val",e.target.value);
        }
        onSubmit = (e) =>{
            e.preventDefault();
            const { nickname,email,password} = this.state;
            let subject = 'Computer Science';
            let typeOfStudy = 'postgraduate';
            let fromKingston = "true";
            Axios('post',true,'registerKStudents',{ nickname, email, password,subject,typeOfStudy,fromKingston})
        }
        render(){
            const { handleSubmit, open, close } = this.props;
            const { nickname,email,password,subject,typeOfStudy } = this.state;
            const formStyle = {
                marginTop:'3%',
                marginRight:'10%',
                marginLeft:'10%'
            }
            const fieldStyle = {
                width:'100%'
            }
            return(
                
                <Dialog
                    title="Sign Up"
                    //actions={actionsLogIn}
                    modal={true}
                    open={open}
                >
                    <form onSubmit={this.onSubmit} style={formStyle}>
                        <div>
                            <Field
                                style={fieldStyle}
                                name="nickname"
                                component={TextField}
                                hintText="Nickname"   
                                floatingLabelText="Nickname"
                                onChange={this.onChange}
                                />
                        </div>
                        <div>
                            <Field 
                                style={fieldStyle}
                                name="email"
                                component={TextField}
                                type="email"
                                hintText="Email"
                                floatingLabelText="Email"
                                onChange={this.onChange}
                                />
                        </div>
                        <div>
                            <Field 
                                style={fieldStyle}
                                name="password"
                                component={TextField}
                                type="password"
                                hintText="Password"
                                floatingLabelText="Password"
                                onChange={this.onChange}
                            />
                        </div>
                        <div>
                            <Field
                                style={fieldStyle}
                                name="subject"
                                component={SelectField}
                                hintText="Subject"
                                floatingLabelText="Subject"  
                               
                            >
                            {this.generateMenuItems()}
                            
                            </Field>
                        </div>
                        <div>
                            <Field
                                style={fieldStyle}
                                name="typeOfStudy"
                                component={SelectField}
                                hintText="Type of Study"
                                floatingLabelText="Type of Study"
                                onChange={this.onChange}
                            >      
                                <MenuItem value="postgraduate" primaryText="Postgraduate"/>
                                <MenuItem value="PhD" primaryText="PhD"/>
                                <MenuItem value="foundation" primaryText="Foundation"/>
                            </Field>
                        </div>
                        <div>
                            <FlatButton
                                label="Cancel"
                                primary={true}
                                onClick={close}
                            />
                            <FlatButton
                                label="Submit"
                                primary={true}
                                disabled={false}
                                type="submit"
                                //onClick={this.handleCloseSignUp}
                            />
                            <FlatButton
                                label="Reset"
                                secondary={true}
                                disabled={false}
                            
                            />
                        </div>
                    </form>
                </Dialog>
            )
        }



}

LoginDialog= reduxForm({
    form:'logInForm'
})(LoginDialog) 

export default LoginDialog;