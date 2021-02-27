import React , { Component }  from "react";
import ImagePicker from 'react-image-picker'
import "./LearningOptions.css";
import img1 from "../../assets/images/s1.png"
import img2 from "../../assets/images/s2.png"
import img3 from "../../assets/images/s3.png"

const imageList = [img1, img2, img3]

class ScreenOptions extends Component{
    constructor(props) {
        super(props)
        this.state = {
            image: '',
            images: [],
            max_images: [],
            max_message: ''
        }
    }
    onPickOneImage(image) {
        this.setState({image})
    }

    onPickMultipleImages(images) {
        this.setState({images})
    }

    onPickImagesWithLimit(max_images) {
        this.setState({max_images})
    }

    onPickMaxImages(last_image) {
        let image = JSON.stringify(last_image)
        let max_message = `Max images reached. ${image}`

        this.setState({max_message})
    }

    render() {
        return (
            <div>
                <h1>React Image Picker</h1>
                <h3>Single Select</h3>
                <ImagePicker
                    images={imageList.map((image, i) => ({src: image, value: i}))}
                    onPick={this.onPickOneImage.bind(this)}
                />
                <textarea rows="4" cols="100" value={this.state.image && JSON.stringify(this.state.image)} disabled/>

                <h3>Multiple Select</h3>
                <ImagePicker
                    images={imageList.map((image, i) => ({src: image, value: i}))}
                    onPick={this.onPickMultipleImages.bind(this)}
                    multiple
                />
                <textarea rows="4" cols="100" value={this.state.images && JSON.stringify(this.state.images)} disabled/>

                <h3>Multiple Select with Limit</h3>
                <ImagePicker
                    images={imageList.map((image, i) => ({src: image, value: i}))}
                    onPick={this.onPickImagesWithLimit.bind(this)}
                    maxPicks={2}
                    onMaxPicks={this.onPickMaxImages.bind(this)}
                    multiple
                />
                <textarea rows="4" cols="100" value={this.state.max_images && JSON.stringify(this.state.max_images)} disabled/>
                <textarea rows="4" cols="100" value={this.state.max_message && JSON.stringify(this.state.max_message)} disabled/>
            </div>
        )
    }
}
// export default ScreenOptions;
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

