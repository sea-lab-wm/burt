import React , { Component }  from "react";
import ImagePicker from 'react-image-picker'
import "./SelectOneScreenOption.css";
import img1 from "../../assets/images/s1.png"
import img2 from "../../assets/images/s2.png"
import img3 from "../../assets/images/s3.png"
import 'react-image-picker/dist/index.css'


const imageList = [img1, img2, img3]

class SelectOneScreen extends Component{

    constructor(props) {
        super(props)
        this.state = {
            image: '',
            images: [],
            max_images: [],
            max_message: ''
        }
        this.onPickOneImage = this.onPickOneImage.bind(this)
    }
    onPickOneImage(image) {
        this.setState({image});
        this.handleOneScreen();

    }
    handleOneScreen(){
        this.props.actionProvider.handleOneScreenOption();
    }


    render() {
        return (
            <div>
                <ImagePicker
                    images={imageList.map((image, i) => ({src: image, value: i}))}
                    onPick={this.onPickOneImage}
                    // onClick={this.props.actionProvider.handleOneScreenOption()}/>;
                />

                <button type="button" onClick={() => this.props.actionProvider.handleNoneOfAboveScreensOption()}>none of above</button>


            </div>
        )
    }
}
// eslint-disable-next-line no-undef

export default SelectOneScreen;
// eslint-disable-next-line no-undef
//render(<ScreenOptions/>, document.querySelector('#root'))



// const ScreenOptions = (props) => {
//     const options = [
//
//         {"name": "s1", "logo": s1,  handler: props.actionProvider.handleScreenList, id: 1},
//         {"name": "s2", "logo": s2, handler: () => {}, id: 2},
//         {"name": "s3", "logo": s3, handler: () => {}, id: 3}
//
//     ];
//
//     const optionsMarkup = options.map((option) => (
//             // <button
//             //     className="learning-option-button"
//             //     key={option.id}
//             //     onClick={option.handler}
//             // >
//             //     {option.name}
//             // </button>
//
//             <img src={option.logo}
//                  alt={option.name}
//                  className="learning-option-button"
//                  onClick={option.handler} />
//
//         ));
//
//     const MyComponent = () => (
//         <Select options={optionsMarkup} />
//     )
//     return MyComponent;
//
//
// };

// export default ScreenOptions;

